package io.github.johnnypixelz.utilizer.features.customentities;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import io.github.johnnypixelz.utilizer.serialize.world.Position;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public abstract class CustomEntity<T extends Entity> {
    private Position position;

    private transient boolean isLoaded;
    private transient T entity;

    public CustomEntity(Position position) {
        this.position = position;
        this.isLoaded = false;
    }

    public Position getPosition() {
        return position;
    }

    public Optional<T> getEntity() {
        return Optional.ofNullable(entity);
    }

    public void teleport(CustomEntityManager<?, T> customEntityManager, Position position) {
        unload();

        this.position = position;

        load(customEntityManager);
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    protected void onTick() {
    }

    protected void onRegister() {
    }

    protected void onUnregister() {
    }

    protected void onLoad() {
    }

    protected void onUnload() {
    }

    public void load(CustomEntityManager<?, T> customEntityManager) {
        if (isLoaded) {
            throw new IllegalStateException("Attempted to load an already loaded CustomEntity");
        }

        final Location location = position.toLocation();
        final World world = location.getWorld();
        if (world == null) return;

        try {
            this.entity = spawnEntity(location);
            this.entity.getPersistentDataContainer().set(Provider.getNamespacedKey(customEntityManager.getId()), PersistentDataType.LONG, System.currentTimeMillis());
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }

        this.isLoaded = true;

        try {
            onLoad();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void unload() {
        if (!isLoaded) {
            throw new IllegalStateException("Attempted to unload an already unloaded CustomEntity");
        }

        try {
            onUnload();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.isLoaded = false;

        if (this.entity != null) {
            this.entity.remove();
            this.entity = null;
        }
    }

    protected abstract T spawnEntity(Location location);

}
