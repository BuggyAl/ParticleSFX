package hm.zelha.particlesfx.particles.parents;

import hm.zelha.particlesfx.util.LVMath;
//import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
//import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;
//import net.minecraft.resources.MinecraftKey;
//import net.minecraft.server.level.EntityPlayer;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
//import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
//import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Particle {

    protected final ThreadLocalRandom rng = ThreadLocalRandom.current();
    protected final Vector fakeOffsetHelper = new Vector();
    protected final Vector xyzHelper = new Vector();
    protected final Vector offsetHelper = new Vector();
    protected ParticleOptions particle;
    protected double offsetX;
    protected double offsetY;
    protected double offsetZ;
    protected double speed = 0;
    protected int count;
    protected int radius = 0;
    private final List<CraftPlayer> players = ((CraftServer) Bukkit.getServer()).getOnlinePlayers();
    private final List<CraftPlayer> listHelper = new ArrayList<>();

    protected Particle(String particleID, double offsetX, double offsetY, double offsetZ, int count) {
        this.particle = (ParticleOptions) BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.fromNamespaceAndPath("minecraft", particleID));
        setOffset(offsetX, offsetY, offsetZ);
        setCount(count);
    }

    public void display(Location location) {
        display(location, players);
    }

    public void displayForPlayers(Location location, Player... players) {
        listHelper.clear();

        for (int i = 0; i < players.length; i++) {
            listHelper.add((CraftPlayer) players[i]);
        }

        display(location, listHelper);
    }

    public void displayForPlayers(Location location, List<UUID> players) {
        listHelper.clear();

        for (int i = 0; i < players.size(); i++) {
            Player p = Bukkit.getPlayer(players.get(i));

            if (p == null) continue;

            listHelper.add((CraftPlayer) p);
        }

        display(location, listHelper);
    }

    /**
     * @param particle particle for this object to copy data from
     * @return this object
     */
    public Particle inherit(Particle particle) {
        offsetX = particle.offsetX;
        offsetY = particle.offsetY;
        offsetZ = particle.offsetZ;
        speed = particle.speed;
        count = particle.count;
        radius = particle.radius;

        return this;
    }

    public abstract Particle clone();

    protected void display(Location location, List<CraftPlayer> players) {
        Validate.notNull(location, "Location cannot be null!");
        Validate.notNull(location.getWorld(), "World cannot be null!");

        for (int i = 0; i < ((getPacketCount() != count) ? count : 1); i++) {
            for (CraftPlayer craftPlayer : players) {
                ServerPlayer player = craftPlayer.getHandle();

                if (player == null) continue;
                if (!location.getWorld().getName().equals(player.level().getWorld().getName())) continue;

                if (radius != 0) {
                    double distance = Math.pow(location.getX() - player.trackingPosition().x(), 2) +
                            Math.pow(location.getY() - player.trackingPosition().y(), 2) +
                            Math.pow(location.getZ() - player.trackingPosition().z(), 2);

                    if (distance > Math.pow(radius, 2)) continue;
                }

                Vector xyz = getXYZ(location);
                Vector offsets = getOffsets(location);
                Packet<?> strangePacket = getStrangePacket(location);

                player.connection.send(Objects.requireNonNullElseGet(strangePacket, () -> new ClientboundLevelParticlesPacket(
                        particle, true, (float) xyz.getX(), (float) xyz.getY(), (float) xyz.getZ(), (float) offsets.getX(),
                        (float) offsets.getY(), (float) offsets.getZ(), getPacketSpeed(), getPacketCount()
                )));
            }
        }
    }

    /** @return a vector meant to be added to a location to mimic particle offset */
    protected Vector generateFakeOffset() {
        fakeOffsetHelper.setX(rng.nextGaussian() * offsetX);
        fakeOffsetHelper.setY(rng.nextGaussian() * offsetY);
        fakeOffsetHelper.setZ(rng.nextGaussian() * offsetZ);

        return fakeOffsetHelper;
    }

    /**
     * Meant to be overridden by child classes to modify the X/Y/Z values of the packet sent to the player.
     *
     * @param location the location passed into the display method
     * @return the xyzHelper with the modified X/Y/Z values
     */
    protected Vector getXYZ(Location location) {
        return LVMath.toVector(xyzHelper, location);
    }

    /**
     * Meant to be overridden by child classes to modify the offset values of the packet sent to the player.
     *
     * @param location the location passed into the display method
     * @return the offsetHelper with the modified offset values
     */
    protected Vector getOffsets(Location location) {
        return offsetHelper.setX(offsetX).setY(offsetY).setZ(offsetZ);
    }

    /**
     * Meant to be overridden by child classes to modify the speed value of the packet sent to the player.
     *
     * @return the modified speed value
     */
    protected float getPacketSpeed() {
        return (float) speed;
    }

    /**
     * Meant to be overridden by child classes to modify the count value of the packet sent to the player.
     *
     * @return the modified count value
     */
    protected int getPacketCount() {
        return count;
    }

    /**
     * Meant to be overridden by child classes when a different packet is needed to display the particle.
     *
     * @param location the location passed into the display method
     * @return the different packet
     */
    protected Packet getStrangePacket(Location location) {
        return null;
    }

    public void setOffset(double x, double y, double z) {
        setOffsetX(x);
        setOffsetY(y);
        setOffsetZ(z);
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = Math.abs(offsetX);
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = Math.abs(offsetY);
    }

    public void setOffsetZ(double offsetZ) {
        this.offsetZ = Math.abs(offsetZ);
    }

    public Particle setSpeed(double speed) {
        this.speed = speed;

        return this;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Particle setRadius(int radius) {
        this.radius = radius;

        return this;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getOffsetZ() {
        return offsetZ;
    }

    public double getSpeed() {
        return speed;
    }

    public int getCount() {
        return count;
    }

    public int getRadius() {
        return radius;
    }
}