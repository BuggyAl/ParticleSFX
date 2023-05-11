package hm.zelha.particlesfx.particles;

import hm.zelha.particlesfx.particles.parents.Particle;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.MinecraftKey;

public class ParticleHeart extends Particle {
    public ParticleHeart(double offsetX, double offsetY, double offsetZ, int count) {
        super("heart", offsetX, offsetY, offsetZ, 1, count, 0);
    }

    public ParticleHeart(double offsetX, double offsetY, double offsetZ) {
        this(offsetX, offsetY, offsetZ, 1);
    }

    public ParticleHeart(int count) {
        this(0, 0, 0, count);
    }

    public ParticleHeart() {
        this(0, 0, 0, 1);
    }

    @Override
    public ParticleHeart inherit(Particle particle) {
        super.inherit(particle);

        return this;
    }

    @Override
    public ParticleHeart clone() {
        return new ParticleHeart().inherit(this);
    }
}
