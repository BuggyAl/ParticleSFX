package hm.zelha.particlesfx.particles;

import hm.zelha.particlesfx.particles.parents.Particle;
import net.minecraft.server.v1_14_R1.Particles;

public class ParticleBarrier extends Particle {
    public ParticleBarrier(double offsetX, double offsetY, double offsetZ, int count) {
        super(Particles.BARRIER, offsetX, offsetY, offsetZ, count);
    }

    public ParticleBarrier(double offsetX, double offsetY, double offsetZ) {
        this(offsetX, offsetY, offsetZ, 1);
    }

    public ParticleBarrier(int count) {
        this(0, 0, 0, count);
    }

    public ParticleBarrier() {
        this(0, 0, 0, 1);
    }

    @Override
    public ParticleBarrier inherit(Particle particle) {
        super.inherit(particle);

        return this;
    }

    @Override
    public ParticleBarrier clone() {
        return new ParticleBarrier().inherit(this);
    }
}