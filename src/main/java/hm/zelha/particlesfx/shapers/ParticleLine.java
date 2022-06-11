package hm.zelha.particlesfx.shapers;

import hm.zelha.particlesfx.Main;
import org.apache.commons.lang3.Validate;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class ParticleLine {

    private BukkitTask animator = null;
    private Effect particle;
    private Location originalStart;
    private Location originalEnd;
    private Location start;
    private Location end;
    private double pitch = 0;
    private double yaw = 0;
    private double roll = 0;
    private double frequency;

    public ParticleLine(Effect particle, Location start, Location end, double frequency) {
        Validate.notNull(particle, "Particle cannot be null!");
        Validate.isTrue(particle.getType() != Effect.Type.SOUND, "Effect must be of Type.PARTICLE or Type.VISUAL!");
        Validate.notNull(start, "Location cannot be null!");
        Validate.notNull(end, "Location cannot be null!");
        Validate.notNull(start.getWorld(), "Location's world cannot be null!");
        Validate.notNull(end.getWorld(), "Location's world cannot be null!");
        Validate.isTrue(frequency > 0.0D, "Frequency cannot be 0 or less!");

        this.particle = particle;
        this.originalStart = start;
        this.originalEnd = end;
        this.start = start;
        this.end = end;
        this.frequency = frequency;

        start();
    }

    public ParticleLine(Effect particle, Location start, Location end) {
        this(particle, start, end, 0.25);
    }

    public void start() {
        if (animator != null) return;

        animator = new BukkitRunnable() {
            @Override
            public void run() {
                Location particleSpawn = start.clone();
                double distance = start.distance(end);
                Vector addition = end.clone().subtract(start).toVector().normalize().multiply(frequency);

                for (double length = 0; length < distance; length += frequency, particleSpawn.add(addition)) {
                    start.getWorld().spigot().playEffect(particleSpawn, particle, 0, 0, 0, 0, 0, 0, 1, 150);
                }
            }
        }.runTaskTimer(Main.getPlugin(), 0, 1);
    }

    public void stop() {
        if (animator == null) return;

        animator.cancel();
        animator = null;
    }

    public void rotateAroundLocation(Location around, double pitch, double yaw, double roll) {
        this.pitch += pitch;
        this.yaw += yaw;
        this.roll += roll;

        for (Location l : new Location[] {originalStart, originalEnd}) {
            double x, y, z, cos, sin, angle;
            Vector v = l.clone().subtract(around).toVector();

            if (this.pitch != 0) {
                angle = Math.toRadians(this.pitch);
                cos = Math.cos(angle);
                sin = Math.sin(angle);
                y = v.getY() * cos - v.getZ() * sin;
                z = v.getY() * sin + v.getZ() * cos;

                v.setY(y).setZ(z);
            }

            if (this.yaw != 0) {
                angle = Math.toRadians(-this.yaw);
                cos = Math.cos(angle);
                sin = Math.sin(angle);
                x = v.getX() * cos + v.getZ() * sin;
                z = v.getX() * -sin + v.getZ() * cos;

                v.setX(x).setZ(z);
            }

            if (this.roll != 0) {
                angle = Math.toRadians(this.roll);
                cos = Math.cos(angle);
                sin = Math.sin(angle);
                x = v.getX() * cos - v.getY() * sin;
                y = v.getX() * sin + v.getY() * cos;

                v.setX(x).setY(y);
            }

            if (l.equals(originalStart)) {
                start = around.clone().add(v);
            } else {
                end = around.clone().add(v);
            }
        }
    }

    public void adjustPitch(double pitch) {
        rotateAroundLocation(originalStart.clone().add(originalEnd).multiply(0.5), pitch, 0, 0);
    }

    public void adjustYaw(double yaw) {
        rotateAroundLocation(originalStart.clone().add(originalEnd).multiply(0.5), 0, yaw, 0);
    }

    public void adjustRoll(double roll) {
        rotateAroundLocation(originalStart.clone().add(originalEnd).multiply(0.5), 0, 0, roll);
    }

    public void setParticle(Effect particle) {
        Validate.isTrue(particle.getType() == Effect.Type.PARTICLE, "Effect must be of Type.PARTICLE!");

        this.particle = particle;
    }

    public void setStart(Location start) {
        Validate.isTrue(start.getWorld() == end.getWorld(), "Locations cannot be in different worlds!");

        this.start = start;
        this.originalStart = start;
        this.originalEnd = end;
        this.pitch = 0;
        this.yaw = 0;
        this.roll = 0;
    }

    public void setEnd(Location end) {
        Validate.isTrue(start.getWorld() == end.getWorld(), "Locations cannot be in different worlds!");

        this.end = end;
        this.originalStart = start;
        this.originalEnd = end;
        this.pitch = 0;
        this.yaw = 0;
        this.roll = 0;
    }

    public void setFrequency(double frequency) {
        Validate.isTrue(frequency <= 0, "Frequency cannot be 0 or less!");

        this.frequency = frequency;
    }

    public Effect getParticle() {
        return particle;
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }

    public double getFrequency() {
        return frequency;
    }
}