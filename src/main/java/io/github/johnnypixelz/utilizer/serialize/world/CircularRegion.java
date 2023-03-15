package io.github.johnnypixelz.utilizer.serialize.world;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.johnnypixelz.utilizer.gson.GsonSerializable;
import io.github.johnnypixelz.utilizer.gson.JsonBuilder;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An immutable and serializable circular region object
 */
public class CircularRegion implements GsonSerializable {
    public static CircularRegion deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("center"));
        Preconditions.checkArgument(object.has("radius"));

        Point center = Point.deserialize(object.get("center"));
        double radius = object.get("radius").getAsDouble();

        return of(center, radius);
    }

    public static CircularRegion of(Point center, double radius) {
        Objects.requireNonNull(center, "center");
        if (radius <= 0) throw new IllegalArgumentException("radius cannot be negative");
        return new CircularRegion(center, radius);
    }

    private final Point center;
    private final double radius;
    private final double diameter;

    private CircularRegion(Point center, double radius) {
        this.center = center;
        this.radius = radius;
        this.diameter = radius * 2;
    }

    /**
     * Determines if the specified {@link Point} is within the region
     * @param pos target position
     * @return true if the position is in the region
     */
    public boolean inRegion(Point pos) {
        Objects.requireNonNull(pos, "pos");
        return pos.distanceSquared(this.center) < this.radius * this.radius;
    }

    /**
     * Determines if the specified {@link Block} is within the region
     * @param block target block
     * @return true if the block is in the region
     */
    public boolean inRegion(Block block) {
        Objects.requireNonNull(block, "block");
        return block.getLocation().distanceSquared(this.center.toLocation()) < this.radius * radius;
    }

    /**
     * The center of the region as a {@link Point}
     * @return the center
     */
    public Point getCenter() {
        return this.center;
    }

    /**
     * The radius of the region
     * @return the radius
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * The diameter of the region
     * @return the diameter
     */
    public double getDiameter() {
        return this.diameter;
    }

    /**
     * The circumference of the region
     * @return the circumference
     */
    public double getCircumference() {
        return 2 * Math.PI * this.radius;
    }

    /**
     * Get the circumference {@link BlockPosition} of the region
     * @return the {@link BlockPosition}s
     */
    @NotNull
    public Set<BlockPosition> getOuterBlockPositions() {
        Set<BlockPosition> positions = new HashSet<>((int) getCircumference());
        for (int degree = 0; degree < 360; degree++) {
            double radian = Math.toRadians(degree);

            double x = Math.cos(radian) * this.radius;
            double z = Math.sin(radian) * this.radius;

            positions.add(this.center.add((int) x, 0, (int) z).floor());
        }
        return Collections.unmodifiableSet(positions);
    }

    @NotNull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                .add("center", this.center)
                .add("radius", this.radius)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CircularRegion that = (CircularRegion) o;
        return Double.compare(that.radius, this.radius) == 0 &&
                center.equals(that.center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.center, this.radius);
    }

    @Override
    public String toString() {
        return "CircularRegion{" +
                "center=" + center +
                ", radius=" + radius +
                '}';
    }

}