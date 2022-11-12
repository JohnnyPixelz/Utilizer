package io.github.johnnypixelz.utilizer.minigame.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

@Deprecated
public class Position {
    private final UUID worldUUID;
    private final double x;
    private final double y;
    private final double z;
    private final float pitch;
    private final float yaw;

    public Position(Location location) {
        this(location.getWorld().getUID(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public Position(UUID worldUUID, double x, double y, double z, float yaw, float pitch) {
        this.worldUUID = worldUUID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
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

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public UUID getWorldUID() {
        return worldUUID;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldUUID);
    }

    public Location toLocation() {
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }
}
