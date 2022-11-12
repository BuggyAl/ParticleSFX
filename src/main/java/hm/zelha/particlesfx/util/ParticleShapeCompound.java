package hm.zelha.particlesfx.util;

import hm.zelha.particlesfx.shapers.parents.RotationHandler;
import hm.zelha.particlesfx.shapers.parents.Shape;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public class ParticleShapeCompound extends RotationHandler implements Shape {

    private final Map<Shape, Integer> shapeLocationIndex = new LinkedHashMap<>();
    private boolean recalc = false;

    public ParticleShapeCompound(Shape... shapes) {
        for (Shape shape : shapes) {
            addShape(shape);
        }
    }

    @Override
    public void start() {
        for (Shape shape : shapeLocationIndex.keySet()) {
            shape.start();
        }
    }

    @Override
    public void stop() {
        for (Shape shape : shapeLocationIndex.keySet()) {
            shape.stop();
        }
    }

    @Override
    public void display() {
        for (Shape shape : shapeLocationIndex.keySet()) {
            shape.display();
        }
    }

    @Override
    public void rotate(double pitch, double yaw, double roll) {
        super.rotate(pitch, yaw, roll);

        for (Shape shape : shapeLocationIndex.keySet()) {
            if (shape.getLocationAmount() == 1) {
                shape.setRotation(rot.getPitch(), rot.getYaw(), rot.getRoll());
            }
        }
    }

    @Override
    public Shape clone() {
        Shape[] shapes = new Shape[shapeLocationIndex.size()];

        int i = 0;

        for (Shape shape : shapeLocationIndex.keySet()) {
            shapes[i] = shape.clone();
            i++;
        }

        return new ParticleShapeCompound(shapes);
    }

    @Override
    protected boolean recalculateIfNeeded(@Nullable Location around) {
        boolean aroundHasChanged = false;

        if (around != null) {
            lastRotatedAround.setPitch(around.getPitch());
            lastRotatedAround.setYaw(around.getYaw());

            if (!around.equals(lastRotatedAround)) {
                aroundHasChanged = true;

                lastRotatedAround.zero().add(around);
            }
        }

        if (locations.size() != 1 && recalc) {
            //need to set both origins to what they would be if the rotation was 0, 0, 0
            //aka, inverse the current rotation for rot, apply it to all locations, and set that as the origin for rot
            //then we inverse the rotation for rot2, apply that to rot's origins using the last rotated around location,
            //and set that as the origins for rot2
            Rotation rotHelper = LocationSafe.getRotHelper();

            rotHelper.set(-rot.getPitch(), -rot.getYaw(), -rot.getRoll());
            calculateCentroid(locations);

            for (int i = 0; i < locations.size(); i++) {
                Vector v = LVMath.subtractToVector(rhVectorHelper, locations.get(i), centroid);

                rotHelper.applyRoll(v);
                rotHelper.applyYaw(v);
                rotHelper.applyPitch(v);

                LVMath.additionToLocation(origins.get(i), centroid, v);
            }
        }

        if (recalc || aroundHasChanged) {
            //get origin centroid, set vectorHelper to the distance between centroid and lastRotatedAround, rotate vectorHelper by the
            //inverse of rot2, set originalCentroid to lastRotatedAround + vectorHelper
            Rotation rotHelper = LocationSafe.getRotHelper();

            if (locations.size() == 1) {
                centroid.zero().add(locations.get(0));
            } else {
                calculateCentroid(origins);
            }

            rotHelper.set(-rot2.getPitch(), -rot2.getYaw(), -rot2.getRoll());
            LVMath.subtractToVector(rhVectorHelper, centroid, lastRotatedAround);
            rotHelper.applyRoll(rhVectorHelper);
            rotHelper.applyYaw(rhVectorHelper);
            rotHelper.applyPitch(rhVectorHelper);
            LVMath.additionToLocation(originalCentroid, lastRotatedAround, rhVectorHelper);
        }

        return recalc;
    }

    /**
     * wondering why im using reflection? laziness! <p></p>
     *
     * i REALLY, *REALLY* do not want to make a List class that can handle everything the end user
     * can do without breaking everything. <p></p>
     *
     * i might later. at some point. we'll see. but im already going insane enough trying to make this spawn of hell.
     */
    private ArrayListSafe<LocationSafe> reflectLocations(Shape shape) {
        try {
            Field f = RotationHandler.class.getDeclaredField("locations");

            f.setAccessible(true);

            return (ArrayListSafe<LocationSafe>) f.get(shape);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Something went wrong getting shape's locations, this should never happen", ex);

            return null;
        }
    }

    public void addShape(Shape shape) {
        Validate.notNull(shape, "Shape cannot be null!");

        for (Shape mapShape : shapeLocationIndex.keySet()) {
            Validate.isTrue(shape != mapShape, "ParticleShapeCompounds can't hold the same shape twice!");
        }

        ArrayListSafe<LocationSafe> locations = reflectLocations(shape);

        if (locations == null) return;

        if (!this.locations.isEmpty()) {
            Validate.isTrue(locations.get(0).getWorld().equals(this.locations.get(0).getWorld()), "Locations cannot have differing worlds!");
        } else {
            setWorld(locations.get(0).getWorld());
        }

        if (shapeLocationIndex.isEmpty()) {
            shapeLocationIndex.put(shape, shape.getLocationAmount() - 1);
        } else {
            Shape last = (Shape) shapeLocationIndex.keySet().toArray()[shapeLocationIndex.size() - 1];

            shapeLocationIndex.put(shape, shapeLocationIndex.get(last) + shape.getLocationAmount());
        }

        locations.addAddMechanics(this, (owner, added) -> {
            boolean locationAdded = false;

            for (Map.Entry<Shape, Integer> entry : shapeLocationIndex.entrySet()) {
                if (owner == entry.getKey()) {
                    this.locations.add(entry.getValue(), added);
                    this.origins.add(entry.getValue(), added.cloneToLocation());

                    locationAdded = true;
                }

                if (locationAdded) {
                    entry.setValue(entry.getValue() + 1);
                }
            }
        });

        locations.addRemoveMechanics(this, (owner, index) -> {
            Shape last = null;
            boolean locationRemoved = false;

            for (Map.Entry<Shape, Integer> entry : shapeLocationIndex.entrySet()) {
                if (owner == entry.getKey()) {
                    //adding index to the last pair's index to get the index of the locations in the list
                    int removeIndex = shapeLocationIndex.get(last) + index;

                    this.locations.remove(removeIndex);
                    origins.remove(removeIndex);

                    locationRemoved = true;
                }

                if (locationRemoved) {
                    entry.setValue(entry.getValue() - 1);
                }

                last = entry.getKey();
            }
        });

        for (LocationSafe l : locations) {
            l.addRecalcMechanic(this, (location) -> recalc = true);
            this.locations.add(l);
            origins.add(l.cloneToLocation());
        }

        if (getPitch() + getYaw() + getRoll() + getAroundPitch() + getAroundYaw() + getAroundRoll() != 0) {
            recalc = true;
        }
    }

    public void removeShape(int index) {
        Shape[] arrayKeySet = shapeLocationIndex.keySet().toArray(new Shape[0]);
        ArrayListSafe<LocationSafe> locations = reflectLocations(arrayKeySet[index]);
        int locAmount = arrayKeySet[index].getLocationAmount();
        int firstIndex;

        if (locations == null) return;

        if (index > 0) {
            //getting the location index of the last shape and adding 1
            firstIndex = shapeLocationIndex.get(arrayKeySet[index - 1]) + 1;
        } else {
            firstIndex = 0;
        }

        locations.removeMechanics(this);

        for (LocationSafe l : locations) {
            l.removeRecalcMechanic(this);
            this.locations.remove(firstIndex);
            origins.remove(firstIndex);
        }

        shapeLocationIndex.remove(arrayKeySet[index]);

        for (int i = index; i < shapeLocationIndex.size(); i++) {
            //subtracting locAmount from all shape location indexes at 'index' and beyond
            shapeLocationIndex.put(arrayKeySet[i], shapeLocationIndex.get(arrayKeySet[i]) - locAmount);
        }

        if (getPitch() + getYaw() + getRoll() + getAroundPitch() + getAroundYaw() + getAroundRoll() != 0) {
            recalc = true;
        }
    }

    public Shape getShape(int index) {
        return (Shape) shapeLocationIndex.keySet().toArray()[index];
    }

    public int getShapeAmount() {
        return shapeLocationIndex.size();
    }
}
