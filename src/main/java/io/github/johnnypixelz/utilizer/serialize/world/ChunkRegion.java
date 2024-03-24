package io.github.johnnypixelz.utilizer.serialize.world;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.johnnypixelz.utilizer.gson.GsonSerializable;
import io.github.johnnypixelz.utilizer.gson.JsonBuilder;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An immutable and serializable chunk region object
 */
public class ChunkRegion implements GsonSerializable {
    public static ChunkRegion deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("min"));
        Preconditions.checkArgument(object.has("max"));

        ChunkPosition a = ChunkPosition.deserialize(object.get("min"));
        ChunkPosition b = ChunkPosition.deserialize(object.get("max"));

        return of(a, b);
    }

    public static ChunkRegion of(ChunkPosition a, ChunkPosition b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");

        if (!a.getWorld().equals(b.getWorld())) {
            throw new IllegalArgumentException("positions are in different worlds");
        }

        return new ChunkRegion(a, b);
    }

    private final ChunkPosition min;
    private final ChunkPosition max;

    private final int width;
    private final int depth;

    private ChunkRegion(ChunkPosition a, ChunkPosition b) {
        this.min = ChunkPosition.of(Math.min(a.getX(), b.getX()), Math.min(a.getZ(), b.getZ()), a.getWorld());
        this.max = ChunkPosition.of(Math.max(a.getX(), b.getX()), Math.max(a.getZ(), b.getZ()), a.getWorld());

        this.width = this.max.getX() - this.min.getX() + 1;
        this.depth = this.max.getZ() - this.min.getZ() + 1;
    }

    public boolean inRegion(ChunkPosition pos) {
        Objects.requireNonNull(pos, "pos");
        return pos.getWorld().equals(this.min.getWorld()) && inRegion(pos.getX(), pos.getZ());
    }

    public boolean inRegion(int x, int z) {
        return x >= this.min.getX() && x <= this.max.getX()
                && z >= this.min.getZ() && z <= this.max.getZ();
    }

    public ChunkPosition getMin() {
        return this.min;
    }

    public ChunkPosition getMax() {
        return this.max;
    }

    public int getWidth() {
        return this.width;
    }

    public int getDepth() {
        return this.depth;
    }

    @Nonnull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                .add("min", this.min)
                .add("max", this.max)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ChunkRegion)) return false;
        final ChunkRegion other = (ChunkRegion) o;
        return this.getMin().equals(other.getMin()) && this.getMax().equals(other.getMax());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getMin().hashCode();
        result = result * PRIME + this.getMax().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChunkRegion(min=" + this.getMin() + ", max=" + this.getMax() + ")";
    }

}
