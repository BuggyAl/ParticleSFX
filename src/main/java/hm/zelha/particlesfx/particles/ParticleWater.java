package hm.zelha.particlesfx.particles;

import hm.zelha.particlesfx.particles.parents.TravellingParticle;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * NOTE: velocity on this particle cant be controlled! <p>
 * and thus, both velocity and location to go variables will be completely inaccurate. not my fault <p></p>
 * no matter how low you set it, it'll always go at least 14-21 blocks in the direction its set. blame mojang
 */
public class ParticleWater extends TravellingParticle {
    public ParticleWater(Location toGo, double offsetX, double offsetY, double offsetZ, int count) {
        super(EnumParticle.WATER_WAKE, 0.05, null, toGo, offsetX, offsetY, offsetZ, count);
    }

    public ParticleWater(Vector velocity, double offsetX, double offsetY, double offsetZ, int count) {
        super(EnumParticle.WATER_WAKE, 0.05, velocity, null, offsetX, offsetY, offsetZ, count);
    }

    public ParticleWater(Location toGo, double offsetX, double offsetY, double offsetZ) {
        this(toGo, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleWater(Vector velocity, double offsetX, double offsetY, double offsetZ) {
        this(velocity, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleWater(double offsetX, double offsetY, double offsetZ, int count) {
        this((Location) null, offsetX, offsetY, offsetZ, count);
    }

    public ParticleWater(double offsetX, double offsetY, double offsetZ) {
        this((Location) null, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleWater(int count) {
        this((Location) null, 0, 0, 0, count);
    }

    public ParticleWater(Location toGo) {
        this(toGo, 0, 0, 0, 1);
    }

    public ParticleWater(Vector velocity) {
        this(velocity, 0, 0, 0, 1);
    }

    public ParticleWater() {
        this((Location) null, 0, 0, 0, 1);
    }
}