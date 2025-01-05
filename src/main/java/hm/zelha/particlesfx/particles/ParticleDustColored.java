package hm.zelha.particlesfx.particles;

import hm.zelha.particlesfx.particles.parents.ColorableParticle;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.particles.parents.SizeableParticle;
import hm.zelha.particlesfx.util.Color;
import net.minecraft.core.particles.DustParticleOptions;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleDustColored extends ColorableParticle implements SizeableParticle {

    protected boolean pureColor = false;
    protected double size;

    public ParticleDustColored(@Nullable Color color, double size, double offsetX, double offsetY, double offsetZ, int count) {
        super("", color, offsetX, offsetY, offsetZ, count);

        particle = new ParticleParamDust(color, size, pureColor);

        setSize(size);
    }

    public ParticleDustColored(double size, double offsetX, double offsetY, double offsetZ, int count) {
        this(null, size, offsetX, offsetY, offsetZ, count);
    }

    public ParticleDustColored(@Nullable Color color, double offsetX, double offsetY, double offsetZ, int count) {
        this(color, 1, offsetX, offsetY, offsetZ, count);
    }

    public ParticleDustColored(double offsetX, double offsetY, double offsetZ, int count) {
        this(null, 1, offsetX, offsetY, offsetZ, count);
    }

    public ParticleDustColored(@Nullable Color color, double offsetX, double offsetY, double offsetZ) {
        this(color, 1, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleDustColored(double size, double offsetX, double offsetY, double offsetZ) {
        this(null, size, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleDustColored(double offsetX, double offsetY, double offsetZ) {
        this(null, 1, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleDustColored(@Nullable Color color, double size, int count) {
        this(color, size, 0, 0, 0, count);
    }

    public ParticleDustColored(@Nullable Color color, double size) {
        this(color, size, 0, 0, 0, 1);
    }

    public ParticleDustColored(@Nullable Color color) {
        this(color, 1, 0, 0, 0, 1);
    }

    public ParticleDustColored(int count) {
        this(null, 1, 0, 0, 0, count);
    }

    public ParticleDustColored(double size) {
        this(null, size, 0, 0, 0, 1);
    }

    public ParticleDustColored() {
        this(null, 1, 0, 0, 0, 1);
    }

    @Override
    public ParticleDustColored inherit(Particle particle) {
        super.inherit(particle);

        if (particle instanceof ParticleDustColored) {
            pureColor = ((ParticleDustColored) particle).pureColor;
        }

        if (particle instanceof SizeableParticle) {
            setSize(((SizeableParticle) particle).getSize());
        }

        return this;
    }

    @Override
    public ParticleDustColored clone() {
        return new ParticleDustColored().inherit(this);
    }

    @Override
    protected void display(Location location, List<CraftPlayer> players) {
        if (particle instanceof ParticleParamDust) {
            ParticleParamDust dust = ((ParticleParamDust) particle);

            if (color == null || !color.equals(dust.color) || size != dust.size || pureColor != dust.pureColor) {
                particle = new ParticleParamDust(color, size, pureColor);
            }
        }

        super.display(location, players);
    }

    /** only changes between 0 and 4. */
    @Override
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * if this is set to true, the particle will use derivatives of Float.MAX_VALUE to try and eliminate the variations
     * in color when normally using colored dust.
     * aka no purple or brown or other colors like that
     *
     * @param pureColor whether the color should be pure
     * @return this object
     */
    public ParticleDustColored setPureColor(boolean pureColor) {
        this.pureColor = pureColor;

        return this;
    }

    /** only changes between 0 and 4. */
    @Override
    public double getSize() {
        return size;
    }

    /**
     * if this is set to true, the particle will use derivatives of Float.MAX_VALUE to try and eliminate the variations
     * in color when normally using colored dust.
     * aka no purple or brown or other colors like that
     *
     * @return whether the color should be pure
     */
    public boolean isPureColor() {
        return pureColor;
    }


    private static class ParticleParamDust extends DustParticleOptions {

        private static final ThreadLocalRandom rng = ThreadLocalRandom.current();
        private final Color color;
        private final double size;
        private final boolean pureColor;

        public ParticleParamDust(Color color, double size, boolean pureColor) {
            super(new Vector3f(rng.nextFloat(), rng.nextFloat(), rng.nextFloat()), (float) size);

            this.color = (color != null) ? color.clone() : null;
            this.size = size;
            this.pureColor = pureColor;

            if (color != null) {
                getColor().set(color.getRed(), color.getGreen(), color.getBlue());
                getColor().div(255F);
            }

            if (pureColor) {
                getColor().mul(Float.MAX_VALUE);
            }
        }
    }
}