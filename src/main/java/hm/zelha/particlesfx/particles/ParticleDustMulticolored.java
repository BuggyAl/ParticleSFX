package hm.zelha.particlesfx.particles;

import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.particles.parents.SizeableParticle;
import hm.zelha.particlesfx.util.Color;
import net.minecraft.core.particles.DustColorTransitionOptions;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleDustMulticolored extends ParticleDustColored implements SizeableParticle {

    private Color transition = null;

    public ParticleDustMulticolored(@Nullable Color color, double size, double offsetX, double offsetY, double offsetZ, int count) {
        super(color, size, offsetX, offsetY, offsetZ, count);

        particle = new ParticleParamDustTransition(color, transition, size, pureColor);
    }

    public ParticleDustMulticolored(double size, double offsetX, double offsetY, double offsetZ, int count) {
        this(null, size, offsetX, offsetY, offsetZ, count);
    }

    public ParticleDustMulticolored(@Nullable Color color, double offsetX, double offsetY, double offsetZ, int count) {
        this(color, 1, offsetX, offsetY, offsetZ, count);
    }

    public ParticleDustMulticolored(double offsetX, double offsetY, double offsetZ, int count) {
        this(null, 1, offsetX, offsetY, offsetZ, count);
    }

    public ParticleDustMulticolored(@Nullable Color color, double offsetX, double offsetY, double offsetZ) {
        this(color, 1, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleDustMulticolored(double size, double offsetX, double offsetY, double offsetZ) {
        this(null, size, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleDustMulticolored(double offsetX, double offsetY, double offsetZ) {
        this(null, 1, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleDustMulticolored(@Nullable Color color, double size, int count) {
        this(color, size, 0, 0, 0, count);
    }

    public ParticleDustMulticolored(@Nullable Color color, double size) {
        this(color, size, 0, 0, 0, 1);
    }

    public ParticleDustMulticolored(@Nullable Color color) {
        this(color, 1, 0, 0, 0, 1);
    }

    public ParticleDustMulticolored(int count) {
        this(null, 1, 0, 0, 0, count);
    }

    public ParticleDustMulticolored(double size) {
        this(null, size, 0, 0, 0, 1);
    }

    public ParticleDustMulticolored() {
        this(null, 1, 0, 0, 0, 1);
    }

    @Override
    public ParticleDustMulticolored inherit(Particle particle) {
        super.inherit(particle);

        if (particle instanceof ParticleDustMulticolored) {
            this.transition = ((ParticleDustMulticolored) particle).transition;
        }

        return this;
    }

    @Override
    public ParticleDustMulticolored clone() {
        return new ParticleDustMulticolored().inherit(this);
    }

    @Override
    protected void display(Location location, List<CraftPlayer> players) {
        ParticleParamDustTransition p = (ParticleParamDustTransition) particle;

        if (color == null || transition == null || !color.equals(p.color) || !transition.equals(p.transition) || size != p.size || pureColor != p.pureColor) {
            particle = new ParticleParamDustTransition(color, transition, size, pureColor);
        }

        super.display(location, players);
    }

    /**
     * @param transition the color this particle will transition to as it fades. doesn't work with pureColor
     */
    public ParticleDustMulticolored setTransitionColor(@Nullable Color transition) {
        this.transition = transition;

        return this;
    }

    /**
     * @return the color this particle will transition to as it fades. doesn't work with pureColor
     */
    public Color getTransitionColor() {
        return transition;
    }


    private static class ParticleParamDustTransition extends DustColorTransitionOptions {

        private static final ThreadLocalRandom rng = ThreadLocalRandom.current();
        private final Color color;
        private final Color transition;
        private final double size;
        private final boolean pureColor;

        private ParticleParamDustTransition(Color color, Color transition, double size, boolean pureColor) {
            super(new Vector3f(rng.nextFloat(), rng.nextFloat(), rng.nextFloat()), new Vector3f(rng.nextFloat(), rng.nextFloat(), rng.nextFloat()), (float) size);

            this.color = (color != null) ? color.clone() : null;
            this.transition = (transition != null) ? transition.clone() : null;
            this.size = size;
            this.pureColor = pureColor;

            if (color != null) {
                getFromColor().set(color.getRed(), color.getGreen(), color.getBlue());
                getFromColor().div(255F);
            }

            if (transition != null) {
                getToColor().set(transition.getRed(), transition.getGreen(), transition.getBlue());
                getToColor().div(255F);
            }

            if (pureColor) {
                getFromColor().mul(Float.MAX_VALUE);
                getToColor().mul(Float.MAX_VALUE);
            }
        }
    }
}