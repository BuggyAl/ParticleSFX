package hm.zelha.particlesfx.particles;

import hm.zelha.particlesfx.particles.parents.Particle;
import net.minecraft.server.v1_16_R1.Particles;

public class ParticleFallingHoney extends Particle {
    public ParticleFallingHoney(double offsetX, double offsetY, double offsetZ, int count) {
        super(Particles.FALLING_HONEY, offsetX, offsetY, offsetZ, 0, count, 0);
    }

    public ParticleFallingHoney(double offsetX, double offsetY, double offsetZ) {
        this(offsetX, offsetY, offsetZ, 1);
    }

    public ParticleFallingHoney(int count) {
        this(0, 0, 0, count);
    }

    public ParticleFallingHoney() {
        this(0, 0, 0, 1);
    }

    @Override
    public ParticleFallingHoney inherit(Particle particle) {
        super.inherit(particle);

        return this;
    }

    @Override
    public ParticleFallingHoney clone() {
        return new ParticleFallingHoney().inherit(this);
    }
}