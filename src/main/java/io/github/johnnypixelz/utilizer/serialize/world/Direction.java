package io.github.johnnypixelz.utilizer.serialize.world;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.johnnypixelz.utilizer.gson.GsonSerializable;
import io.github.johnnypixelz.utilizer.gson.JsonBuilder;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An immutable and serializable direction object
 */
public class Direction implements GsonSerializable {
    public static final Direction ZERO = Direction.of(0.0f, 0.0f);

    public static Direction deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("yaw"));
        Preconditions.checkArgument(object.has("pitch"));

        float yaw = object.get("yaw").getAsFloat();
        float pitch = object.get("pitch").getAsFloat();

        return of(yaw, pitch);
    }

    public static Direction of(float yaw, float pitch) {
        return new Direction(yaw, pitch);
    }

    public static Direction from(Location location) {
        Objects.requireNonNull(location, "location");
        return of(location.getYaw(), location.getPitch());
    }

    private final float yaw;
    private final float pitch;

    private Direction(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    @Nonnull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                .add("yaw", this.yaw)
                .add("pitch", this.pitch)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Direction)) return false;
        final Direction other = (Direction) o;
        return Float.compare(this.getYaw(), other.getYaw()) == 0 &&
                Float.compare(this.getPitch(), other.getPitch()) == 0;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;

        result = result * PRIME + Float.floatToIntBits(this.getYaw());
        result = result * PRIME + Float.floatToIntBits(this.getPitch());

        return result;
    }

    @Override
    public String toString() {
        return "Direction(yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ")";
    }

}
