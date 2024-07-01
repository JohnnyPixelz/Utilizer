package io.github.johnnypixelz.utilizer.features.customentities;

import io.github.johnnypixelz.utilizer.file.storage.Storage;
import io.github.johnnypixelz.utilizer.file.storage.container.file.FileStorageContainer;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class CustomEntityManager<T extends CustomEntity<E>, E extends Entity> {
    private final FileStorageContainer<List<T>> storage;
    private final CustomEntityListener<T, E> listener;
    private final String id;

    private boolean autoLoad;

    private BukkitTask tickTask;
    private BukkitTask removeOldEntitiesTask;
    private BukkitTask autoSaveTask;

    public CustomEntityManager(Class<T> customEntityClass, Class<E> entityClass, String id) {
        this.id = id;

        this.storage = Storage.list(customEntityClass)
                .json(id)
                .container(ArrayList::new);

        this.listener = new CustomEntityListener<>(this);

        this.autoLoad = true;
    }

    public CustomEntityManager<T, E> init() {
        if (autoLoad) {
            storage.get().forEach(customEntity -> customEntity.load(this));
        }

        if (tickTask == null) {
            tickTask = Tasks.sync().timer(bukkitTask -> {
                storage.get().forEach(CustomEntity::onTick);
            }, 1L);
        }

        if (removeOldEntitiesTask == null) {
            removeOldEntitiesTask = Tasks.sync().timer(() -> {
                List<Entity> toBeRemoved = new ArrayList<>();
                final NamespacedKey namespacedKey = Provider.getNamespacedKey(id);

                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        final boolean hasData = entity.getPersistentDataContainer().has(namespacedKey, PersistentDataType.LONG);
                        if (!hasData) continue;

                        final boolean isValidEntity = getCustomEntities().stream()
                                .anyMatch(customEntity -> customEntity.getEntity().map(entity::equals).orElse(false));
                        if (!isValidEntity) {
                            toBeRemoved.add(entity);
                        }
                    }
                }

                if (!toBeRemoved.isEmpty()) {
                    toBeRemoved.forEach(Entity::remove);
                }
            }, 20);
        }

        // Registering listener
        Bukkit.getPluginManager().registerEvents(this.listener, Provider.getPlugin());

        return this;
    }

    public String getId() {
        return id;
    }

    public boolean isAutoLoad() {
        return autoLoad;
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
    }

    public CustomEntityManager<T, E> setAutoSave(long ticks) {
        if (autoSaveTask != null && !autoSaveTask.isCancelled()) {
            autoSaveTask.cancel();
        }

        if (ticks <= 0) return this;

        autoSaveTask = Tasks.sync().delayedTimer(bukkitTask -> {
            save();
        }, ticks, ticks);

        return this;
    }

    public void unload() {
        if (tickTask != null) {
            if (!tickTask.isCancelled()) tickTask.cancel();
            tickTask = null;
        }

        if (autoSaveTask != null) {
            if (!autoSaveTask.isCancelled()) autoSaveTask.cancel();
            autoSaveTask = null;
        }

        storage.get().forEach(customEntity -> {
            if (customEntity.isLoaded()) {
                customEntity.unload();
            }
        });

        save();
    }

    public void save() {
        storage.save();
    }

    public List<T> getCustomEntities() {
        return storage.get();
    }

    public T registerCustomEntity(T customEntity) {
        if (storage.get().contains(customEntity)) {
            throw new IllegalArgumentException("Attempted to register an already registered CustomEntity");
        }

        storage.get().add(customEntity);
        customEntity.onRegister();
        customEntity.load(this);

        return customEntity;
    }

    public void unregisterCustomEntity(T customEntity) {
        if (!storage.get().contains(customEntity)) {
            throw new IllegalArgumentException("Attempted to unregister a non-registered CustomEntity");
        }

        customEntity.unload();
        customEntity.onUnregister();
        storage.get().remove(customEntity);
    }

    public CustomEntityListener<T, E> getListener() {
        return this.listener;
    }

}
