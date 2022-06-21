package hm.zelha.particlesfx.shapers;

import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.parents.ParticleShaper;
import hm.zelha.particlesfx.util.LVMath;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;

import java.util.Arrays;

public class ParticleLine extends ParticleShaper {

    private Location start;
    private Location end;

    public ParticleLine(Particle particle, Location start, Location end, double frequency, int particlesPerDisplay) {
        super(particle, 0, 0, 0, frequency, particlesPerDisplay);

        Validate.notNull(start, "Location cannot be null!");
        Validate.notNull(end, "Location cannot be null!");
        Validate.notNull(start.getWorld(), "Location's world cannot be null!");
        Validate.notNull(end.getWorld(), "Location's world cannot be null!");
        rot.addOrigins(start, end);
        rot2.addOrigins(start, end);
        locationHelper.setWorld(start.getWorld());

        this.start = start;
        this.end = end;
    }

    public ParticleLine(Particle particle, Location start, Location end, double frequency) {
        this(particle, start, end, frequency, 0);
    }

    public ParticleLine(Particle particle, Location start, Location end, int particlesPerDisplay) {
        this(particle, start, end, 50, particlesPerDisplay);
    }

    public ParticleLine(Particle particle, Location start, Location end) {
        this(particle, start, end, 50, 0);
    }

    @Override
    public void display() {
        boolean hasRan = false;
        boolean trackCount = particlesPerDisplay > 0;
        double distance = start.distance(end);
        double control = distance / frequency;

        locationHelper.zero().add(start);
        LVMath.subtractToVector(vectorHelper, end, start).normalize().multiply(control);

        if (trackCount) {
            locationHelper.add(vectorHelper.getX() * overallCount, vectorHelper.getY() * overallCount, vectorHelper.getZ() * overallCount);
        }

        for (double length = control * overallCount; length < distance; length += control) {
            if (mechanic != null) mechanic.apply(particle, locationHelper, vectorHelper);

            locationHelper.add(vectorHelper);
            getCurrentParticle().display(locationHelper);

            overallCount++;

            if (trackCount) {
                currentCount++;
                hasRan = true;

                if (currentCount >= particlesPerDisplay) {
                    currentCount = 0;
                    break;
                }
            }
        }

        if (!trackCount) overallCount = 0;
        if (!hasRan && trackCount) overallCount = 0;
    }

    @Override
    public void rotateAroundLocation(Location around, double pitch, double yaw, double roll) {
        rot2.add(pitch, yaw, roll);
        rot2.apply(around, Arrays.asList(start, end));
        rot2.apply(around, rot.getOrigins());
    }

    @Override
    public void rotate(double pitch, double yaw, double roll) {
        rot.add(pitch, yaw, roll);
        rot.apply(locationHelper.zero().add(start).add(end).multiply(0.5), Arrays.asList(start, end));
    }

    @Override
    public void move(double x, double y, double z) {
        rot.moveOrigins(x, y, z);
        rot2.moveOrigins(x, y, z);

        for (Location l : new Location[] {start, end}) l.add(x, y, z);
    }

    public void setStart(Location start) {
        Validate.isTrue(start.getWorld() == end.getWorld(), "Locations cannot be in different worlds!");
        Validate.notNull(start.getWorld(), "Location's world cannot be null!");

        this.start = start;
        rot.reset();
        rot2.reset();
        rot.addOrigins(start, end);
        rot2.addOrigins(start, end);
    }

    public void setEnd(Location end) {
        Validate.isTrue(start.getWorld() == end.getWorld(), "Locations cannot be in different worlds!");
        Validate.notNull(end.getWorld(), "Location's world cannot be null!");

        this.end = end;
        rot.reset();
        rot2.reset();
        rot.addOrigins(start, end);
        rot2.addOrigins(start, end);
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }
}