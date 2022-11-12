package io.github.johnnypixelz.utilizer.serialize.world;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.johnnypixelz.utilizer.cache.Lazy;
import io.github.johnnypixelz.utilizer.gson.GsonSerializable;
import io.github.johnnypixelz.utilizer.gson.JsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * An immutable and serializable chuck location object
 */
public class ChunkPosition implements GsonSerializable {
    public static ChunkPosition deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("x"));
        Preconditions.checkArgument(object.has("z"));
        Preconditions.checkArgument(object.has("world"));

        int x = object.get("x").getAsInt();
        int z = object.get("z").getAsInt();
        String world = object.get("world").getAsString();

        return of(x, z, world);
    }

    public static ChunkPosition of(int x, int z, String world) {
        Objects.requireNonNull(world, "world");
        return new ChunkPosition(x, z, world);
    }

    public static ChunkPosition of(int x, int z, World world) {
        Objects.requireNonNull(world, "world");
        return of(x, z, world.getName());
    }

    public static ChunkPosition of(Chunk location) {
        Objects.requireNonNull(location, "location");
        return of(location.getX(), location.getZ(), location.getWorld().getName());
    }

    public static ChunkPosition of(long encodedLong, String world) {
        Objects.requireNonNull(world, "world");
        return of((int) encodedLong, (int) (encodedLong >> 32), world);
    }

    public static ChunkPosition of(long encodedLong, World world) {
        Objects.requireNonNull(world, "world");
        return of(encodedLong, world.getName());
    }

    private final int x;
    private final int z;
    private final String world;

    private final Lazy<Collection<BlockPosition>> blocks = Lazy.suppliedBy(() -> {
        List<BlockPosition> blocks = new ArrayList<>(16 * 16 * 256);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 256; y++) {
                    blocks.add(getBlock(x, y, z));
                }
            }
        }
        return Collections.unmodifiableList(blocks);
    });

    private ChunkPosition(int x, int z, String world) {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public String getWorld() {
        return this.world;
    }

    public Chunk toChunk() {
        return Bukkit.getWorld(world).getChunkAt(this.x, this.z);
    }

    public boolean contains(BlockPosition block) {
        return equals(block.toChunk());
    }

    public boolean contains(Point point) {
        return equals(point.floor().toChunk());
    }

    public BlockPosition getBlock(int x, int y, int z) {
        return BlockPosition.of((this.x << 4) | (x & 0xF), y, (this.z << 4) | (z & 0xF), this.world);
    }

    public Collection<BlockPosition> blocks() {
        return this.blocks.get();
    }

    public ChunkPosition getRelative(BlockFace face) {
        Preconditions.checkArgument(face != BlockFace.UP && face != BlockFace.DOWN, "invalid face");
        return ChunkPosition.of(this.x + face.getModX(), this.z + face.getModZ(), this.world);
    }

    public ChunkPosition getRelative(BlockFace face, int distance) {
        Preconditions.checkArgument(face != BlockFace.UP && face != BlockFace.DOWN, "invalid face");
        return ChunkPosition.of(this.x + (face.getModX() * distance), this.z + (face.getModZ() * distance), this.world);
    }

    public ChunkPosition add(int x, int z) {
        return ChunkPosition.of(this.x + x, this.z + z, this.world);
    }

    public ChunkPosition subtract(int x, int z) {
        return add(-x, -z);
    }

    public long asEncodedLong() {
        return (long) this.x & 0xffffffffL | ((long) this.z & 0xffffffffL) << 32;
    }

    @NotNull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                .add("x", this.x)
                .add("z", this.z)
                .add("world", this.world)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ChunkPosition)) return false;
        final ChunkPosition other = (ChunkPosition) o;
        return this.getX() == other.getX() && this.getZ() == other.getZ() && this.getWorld().equals(other.getWorld());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getX();
        result = result * PRIME + this.getZ();
        result = result * PRIME + this.getWorld().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChunkPosition(x=" + this.getX() + ", z=" + this.getZ() + ", world=" + this.getWorld() + ")";
    }

}
