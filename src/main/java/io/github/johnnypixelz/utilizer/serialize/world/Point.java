package io.github.johnnypixelz.utilizer.serialize.world;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.johnnypixelz.utilizer.gson.GsonSerializable;
import io.github.johnnypixelz.utilizer.gson.JsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An immutable and serializable location object
 */
public class Point implements GsonSerializable {
    public static Point deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("x"));
        Preconditions.checkArgument(object.has("y"));
        Preconditions.checkArgument(object.has("z"));
        Preconditions.checkArgument(object.has("world"));

        double x = object.get("x").getAsDouble();
        double y = object.get("y").getAsDouble();
        double z = object.get("z").getAsDouble();
        String world = object.get("world").getAsString();

        return of(x, y, z, world);
    }

    public static Point of(double x, double y, double z, String world) {
        Objects.requireNonNull(world, "world");
        return new Point(x, y, z, world);
    }

    public static Point of(double x, double y, double z, World world) {
        Objects.requireNonNull(world, "world");
        return of(x, y, z, world.getName());
    }

    public static Point of(Location location) {
        Objects.requireNonNull(location, "location");
        return of(location.getX(), location.getY(), location.getZ(), location.getWorld().getName());
    }

    public static Point of(Block block) {
        Objects.requireNonNull(block, "block");
        return of(block.getLocation());
    }

    private final double x;
    private final double y;
    private final double z;
    private final String world;

    @Nullable
    private Location bukkitLocation = null;

    private Point(double x, double y, double z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public synchronized Location toLocation() {
        if (this.bukkitLocation == null) {
            this.bukkitLocation = new Location(Bukkit.getWorld(world), this.x, this.y, this.z);
        }

        return this.bukkitLocation.clone();
    }

    public BlockPosition floor() {
        return BlockPosition.of(bukkitFloor(this.x), bukkitFloor(this.y), bukkitFloor(this.z), this.world);
    }

    public Point center() {
        return of(bukkitFloor(this.x) + 0.5, bukkitFloor(this.y) + 0.5, bukkitFloor(this.z) + 0.5, this.world);
    }

    public Point horizontalCenter() {
        return of(bukkitFloor(this.x) + 0.5, this.y, bukkitFloor(this.z) + 0.5, this.world);
    }

    public Point verticalCenter() {
        return of(this.x, bukkitFloor(this.y) + 0.5, this.z, this.world);
    }

    public Point randomNearbyPoint(double x, double y, double z) {
        final double randX = x == 0 ? 0 : ThreadLocalRandom.current().nextDouble(x) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
        final double randY = y == 0 ? 0 : ThreadLocalRandom.current().nextDouble(y) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
        final double randZ = z == 0 ? 0 : ThreadLocalRandom.current().nextDouble(z) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);

        return Point.of(this.x + randX, this.y + randY, this.z + randZ, world);
    }

    public Point getRelative(BlockFace face) {
        Objects.requireNonNull(face, "face");
        return Point.of(this.x + face.getModX(), this.y + face.getModY(), this.z + face.getModZ(), this.world);
    }

    public Point getRelative(BlockFace face, double distance) {
        Objects.requireNonNull(face, "face");
        return Point.of(this.x + (face.getModX() * distance), this.y + (face.getModY() * distance), this.z + (face.getModZ() * distance), this.world);
    }

    public Point add(double x, double y, double z) {
        return Point.of(this.x + x, this.y + y, this.z + z, this.world);
    }

    public Point subtract(double x, double y, double z) {
        return add(-x, -y, -z);
    }

    public Region regionWith(Point other) {
        Objects.requireNonNull(other, "other");
        return Region.of(this, other);
    }

    public Position withDirection(Direction direction) {
        return Position.of(this, direction);
    }

    /**
     * Get the distance between this location and another. The value of this
     * method is not cached and uses a costly square-root function, so do not
     * repeatedly call this method to get the location's magnitude. NaN will
     * be returned if the inner result of the sqrt() function overflows, which
     * will be caused if the distance is too long.
     *
     * @see Vector
     * @param other The other location
     * @return the distance
     * @throws IllegalArgumentException for differing worlds
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Get the squared distance between this location and another.
     *
     * @see Vector
     * @param other The other location
     * @return the distance
     * @throws IllegalArgumentException for differing worlds
     */
    public double distanceSquared(Point other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot measure distance to a null location");
        } else if (other.getWorld() == null || getWorld() == null) {
            throw new IllegalArgumentException("Cannot measure distance to a null world");
        } else if (!other.getWorld().equals(getWorld())) {
            throw new IllegalArgumentException("Cannot measure distance between " + getWorld() + " and " + other.getWorld());
        }

        return NumberConversions.square(x - other.x) + NumberConversions.square(y - other.y) + NumberConversions.square(z - other.z);
    }

    private static int bukkitFloor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    @NotNull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                .add("x", this.x)
                .add("y", this.y)
                .add("z", this.z)
                .add("world", this.world)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Point)) return false;
        final Point other = (Point) o;
        return Double.compare(this.getX(), other.getX()) == 0 &&
                Double.compare(this.getY(), other.getY()) == 0 &&
                Double.compare(this.getZ(), other.getZ()) == 0 &&
                this.getWorld().equals(other.getWorld());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;

        final long x = Double.doubleToLongBits(this.getX());
        final long y = Double.doubleToLongBits(this.getY());
        final long z = Double.doubleToLongBits(this.getZ());

        result = result * PRIME + (int) (x >>> 32 ^ x);
        result = result * PRIME + (int) (y >>> 32 ^ y);
        result = result * PRIME + (int) (z >>> 32 ^ z);
        result = result * PRIME + this.getWorld().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Point(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", world=" + this.getWorld() + ")";
    }

}
