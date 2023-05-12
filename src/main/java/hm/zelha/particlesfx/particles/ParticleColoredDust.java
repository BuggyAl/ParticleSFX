package hm.zelha.particlesfx.particles;

import com.mojang.math.Vector3fa;
import hm.zelha.particlesfx.particles.parents.ColorableParticle;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.particles.parents.SizeableParticle;
import hm.zelha.particlesfx.util.Color;
import hm.zelha.particlesfx.util.LVMath;
import net.minecraft.core.particles.ParticleParamRedstone;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.List;

public class ParticleColoredDust extends ColorableParticle implements SizeableParticle {

    protected boolean pureColor = false;
    protected double size;

    public ParticleColoredDust(@Nullable Color color, double size, double offsetX, double offsetY, double offsetZ, int count) {
        super("", color, 100, offsetX, offsetY, offsetZ, count);

        particle = new ParticleParamDust(1, 1, 1, (float) size, Color.WHITE, false);

        setSize(size);
        setColor(color);

        if (color == null) {
            setColor(Color.WHITE);
        }
    }

    public ParticleColoredDust(double size, double offsetX, double offsetY, double offsetZ, int count) {
        this(null, size, offsetX, offsetY, offsetZ, count);
    }

    public ParticleColoredDust(@Nullable Color color, double offsetX, double offsetY, double offsetZ, int count) {
        this(color, 1, offsetX, offsetY, offsetZ, count);
    }

    public ParticleColoredDust(double offsetX, double offsetY, double offsetZ, int count) {
        this(null, 1, offsetX, offsetY, offsetZ, count);
    }

    public ParticleColoredDust(@Nullable Color color, double offsetX, double offsetY, double offsetZ) {
        this(color, 1, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleColoredDust(double size, double offsetX, double offsetY, double offsetZ) {
        this(null, size, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleColoredDust(double offsetX, double offsetY, double offsetZ) {
        this(null, 1, offsetX, offsetY, offsetZ, 1);
    }

    public ParticleColoredDust(@Nullable Color color, double size, int count) {
        this(color, size, 0, 0, 0, count);
    }

    public ParticleColoredDust(@Nullable Color color, double size) {
        this(color, size, 0, 0, 0, 1);
    }

    public ParticleColoredDust(@Nullable Color color) {
        this(color, 1, 0, 0, 0, 1);
    }

    public ParticleColoredDust(int count) {
        this(null, 1, 0, 0, 0, count);
    }

    public ParticleColoredDust() {
        this(null, 1, 0, 0, 0, 1);
    }

    @Override
    public ParticleColoredDust inherit(Particle particle) {
        super.inherit(particle);

        if (particle instanceof ParticleColoredDust) {
            pureColor = ((ParticleColoredDust) particle).pureColor;
            size = ((ParticleColoredDust) particle).size;
        }

        return this;
    }

    @Override
    public ParticleColoredDust clone() {
        return new ParticleColoredDust().inherit(this);
    }

    @Override
    protected void display(Location location, List<CraftPlayer> players) {
        if (particle instanceof ParticleParamDust) {
            ParticleParamDust dust = (ParticleParamDust) particle;

            if ((color != null && !dust.color.equals(color)) || dust.size != size || dust.pureColor != pureColor) {
                float red = 1, green = 1, blue = 1;

                if (color != null) {
                    red = color.getRed() / 255F;
                    green = color.getGreen() / 255F;
                    blue = color.getBlue() / 255F;
                }

                if (pureColor) {
                    red *= Float.MAX_VALUE;
                    green *= Float.MAX_VALUE;
                    blue *= Float.MAX_VALUE;
                }

                particle = new ParticleParamDust(red, green, blue, (float) size, color, pureColor);
            }
        }

        super.display(location, players);
    }

    @Override
    protected Vector getXYZ(Location location) {
        return LVMath.toVector(xyzHelper, location);
    }

    @Override
    protected Vector getOffsets(Location location) {
        return offsetHelper.zero().setX(offsetX).setY(offsetY).setZ(offsetZ);
    }

    @Override
    protected float getPacketSpeed() {
        return (float) speed;
    }

    @Override
    protected int getPacketCount() {
        return count;
    }

    /** @deprecated Unused in this class. */
    @Deprecated
    @Override
    public void setBrightness(int brightness) {
    }

    /** only changes between 0 and 4. */
    @Override
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * if this is set to true, the display method will use derivatives of Float.MAX_VALUE to try and eliminate the variations
     * in color when normally using reddust.
     * <br><br>
     * however, if this is the case size is unused and will always be 100 internally.
     * <br><br>
     * and you're kinda locked into specific colors because lowering the size will make it not pure
     * aka no purple or brown or other colors like that
     *
     * @param pureColor whether the color should be pure
     * @return this object
     */
    public ParticleColoredDust setPureColor(boolean pureColor) {
        this.pureColor = pureColor;

        return this;
    }

    /** @deprecated Unused in this class. */
    @Deprecated
    @Override
    public int getBrightness() {
        return 100;
    }

    @Override
    public double getSize() {
        return size;
    }

    public boolean isPureColor() {
        return pureColor;
    }


    private static class ParticleParamDust extends ParticleParamRedstone {

        private final Color color;
        private final double size;
        private final boolean pureColor;

        public ParticleParamDust(float r, float g, float b, float size, Color color, boolean pureColor) {
            super(new Vector3fa(r, g, b), size);

            this.color = (color == null) ? Color.WHITE : color.clone();
            this.size = size;
            this.pureColor = pureColor;
        }
    }
}