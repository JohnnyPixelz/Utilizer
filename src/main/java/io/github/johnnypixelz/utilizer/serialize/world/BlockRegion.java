package io.github.johnnypixelz.utilizer.serialize.world;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.johnnypixelz.utilizer.gson.GsonSerializable;
import io.github.johnnypixelz.utilizer.gson.JsonBuilder;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An immutable and serializable block region object
 */
public class BlockRegion implements GsonSerializable {
    public static BlockRegion deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("min"));
        Preconditions.checkArgument(object.has("max"));

        BlockPosition a = BlockPosition.deserialize(object.get("min"));
        BlockPosition b = BlockPosition.deserialize(object.get("max"));

        return of(a, b);
    }

    public static BlockRegion of(BlockPosition a, BlockPosition b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");

        if (!a.getWorld().equals(b.getWorld())) {
            throw new IllegalArgumentException("positions are in different worlds");
        }

        return new BlockRegion(a, b);
    }

    private final BlockPosition min;
    private final BlockPosition max;

    private final int width;
    private final int height;
    private final int depth;

    private BlockRegion(BlockPosition a, BlockPosition b) {
        this.min = BlockPosition.of(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()), a.getWorld());
        this.max = BlockPosition.of(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()), a.getWorld());

        this.width = this.max.getX() - this.min.getX();
        this.height = this.max.getY() - this.min.getY();
        this.depth = this.max.getZ() - this.min.getZ();
    }

    public boolean inRegion(BlockPosition pos) {
        Objects.requireNonNull(pos, "pos");
        return pos.getWorld().equals(this.min.getWorld()) && inRegion(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean inRegion(Block block) {
        Objects.requireNonNull(block, "block");
        return block.getWorld().getName().equals(this.min.getWorld()) && inRegion(block.getX(), block.getY(), block.getZ());
    }

    public boolean inRegion(int x, int y, int z) {
        return x >= this.min.getX() && x <= this.max.getX()
                && y >= this.min.getY() && y <= this.max.getY()
                && z >= this.min.getZ() && z <= this.max.getZ();
    }

    public BlockPosition getMin() {
        return this.min;
    }

    public BlockPosition getMax() {
        return this.max;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
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
        if (!(o instanceof BlockRegion)) return false;
        final BlockRegion other = (BlockRegion) o;
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
        return "BlockRegion(min=" + this.getMin() + ", max=" + this.getMax() + ")";
    }


}
