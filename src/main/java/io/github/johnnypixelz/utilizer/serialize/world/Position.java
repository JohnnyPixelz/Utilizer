package io.github.johnnypixelz.utilizer.serialize.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.johnnypixelz.utilizer.gson.GsonSerializable;
import io.github.johnnypixelz.utilizer.gson.JsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An immutable and serializable point + direction object
 */
public class Position implements GsonSerializable {
    public static Position deserialize(JsonElement element) {
        Point point = Point.deserialize(element);
        Direction direction = Direction.deserialize(element);

        return of(point, direction);
    }

    public static Position of(Point point, Direction direction) {
        Objects.requireNonNull(point, "point");
        Objects.requireNonNull(direction, "direction");
        return new Position(point, direction);
    }

    public static Position of(Location location) {
        Objects.requireNonNull(location, "location");
        return of(Point.of(location), Direction.from(location));
    }

    public static Position of(Entity entity) {
        Objects.requireNonNull(entity, "entity");
        return of(entity.getLocation());
    }

    private final Point point;
    private final Direction direction;

    @Nullable
    private Location bukkitLocation = null;

    private Position(Point point, Direction direction) {
        this.point = point;
        this.direction = direction;
    }

    public Point getPoint() {
        return this.point;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public synchronized Location toLocation() {
        if (this.bukkitLocation == null) {
            this.bukkitLocation = new Location(Bukkit.getWorld(point.getWorld()), this.point.getX(), this.point.getY(), this.point.getZ(), this.direction.getYaw(), this.direction.getPitch());
        }

        return this.bukkitLocation.clone();
    }

    public Position add(double x, double y, double z) {
        return this.point.add(x, y, z).withDirection(this.direction);
    }

    public Position subtract(double x, double y, double z) {
        return this.point.subtract(x, y, z).withDirection(this.direction);
    }

    @Nonnull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                .addAll(this.point.serialize())
                .addAll(this.direction.serialize())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Position)) return false;
        final Position other = (Position) o;
        return this.getPoint().equals(other.getPoint()) && this.getDirection().equals(other.getDirection());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;

        result = result * PRIME + this.getPoint().hashCode();
        result = result * PRIME + this.getDirection().hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "Position(point=" + this.getPoint() + ", direction=" + this.getDirection() + ")";
    }

}
