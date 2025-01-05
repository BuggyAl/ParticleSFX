package hm.zelha.particlesfx.particles;

import hm.zelha.particlesfx.particles.parents.ColorableParticle;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.util.Color;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class ParticleEffectColored extends ColorableParticle {

    private final net.minecraft.core.particles.ParticleType<ColorParticleOption> registryParticle = (net.minecraft.core.particles.ParticleType<ColorParticleOption>) BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.fromNamespaceAndPath("minecraft", "entity_effect"));
    private int transparency = 255;

    public ParticleEffectColored(@Nullable Color color, int transparency, double offsetX, double offsetY, double offsetZ, int count) {
        super("", color, offsetX, offsetY, offsetZ, count);

        particle = ColorParticleOption.create(registryParticle, transparency << 24 | ((color != null) ? color.getRGB() : Color.WHITE.getRGB()));

        setTransparency(transparency);
    }

    public ParticleEffectColored(int transparency, double offsetX, double offsetY, double offsetZ, int count) {
        this(null, transparency, offsetX, offsetY, offsetZ, count);
    }

    public ParticleEffectColored(@Nullable Color color, double offsetX, double offsetY, double offsetZ) {
        this(color, 255, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleEffectColored(int transparency, double offsetX, double offsetY, double offsetZ) {
        this(null, transparency, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleEffectColored(double offsetX, double offsetY, double offsetZ, int count) {
        this(null, 255, offsetX, offsetY, offsetZ, count);
    }

    public ParticleEffectColored(double offsetX, double offsetY, double offsetZ) {
        this(null, 255, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleEffectColored(@Nullable Color color) {
        this(color, 255, 0, 0, 0, 1);
    }

    public ParticleEffectColored(int count) {
        this(null, 255, 0, 0, 0, count);
    }

    public ParticleEffectColored() {
        this(null, 255, 0, 0, 0, 1);
    }

    @Override
    public ParticleEffectColored inherit(Particle particle) {
        super.inherit(particle);

        return this;
    }

    @Override
    public ParticleEffectColored clone() {
        return new ParticleEffectColored().inherit(this);
    }

    @Override
    protected void display(Location location, List<CraftPlayer> players) {
        ColorParticleOption colorParticleOption = (ColorParticleOption) particle;

        if (color == null) {
            particle = ColorParticleOption.create(registryParticle, transparency << 24 | rng.nextInt(0xffffff));
        } else if (colorParticleOption.getRed() * 255 != color.getRed() || colorParticleOption.getGreen() * 255 != color.getGreen() || colorParticleOption.getBlue() * 255 != color.getBlue() || colorParticleOption.getAlpha() * 255 != transparency) {
            particle = ColorParticleOption.create(registryParticle, transparency << 24 | color.getRGB());
        }

        super.display(location, players);
    }

    /**
     * @param transparency the transparency of the particle, from 0 to 255
     * @return this object
     */
    public ParticleEffectColored setTransparency(int transparency) {
        this.transparency = Math.min(Math.max(0, transparency), 255);

        return this;
    }

    /**
     * @return the transparency of the particle, from 0 to 255
     */
    public int getTransparency() {
        return transparency;
    }
}