package io.github.johnnypixelz.utilizer.features.customblocks;

import io.github.johnnypixelz.utilizer.file.storage.Storage;
import io.github.johnnypixelz.utilizer.file.storage.container.file.FileStorageContainer;
import io.github.johnnypixelz.utilizer.gson.GsonProvider;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import io.github.johnnypixelz.utilizer.serialize.world.BlockPosition;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class CustomBlockManager<T extends CustomBlock> {
    private final FileStorageContainer<Map<BlockPosition, T>> storage;
    private BukkitTask tickTask;
    private BukkitTask autoSaveTask;
    private final CustomBlockItemSupplier<T> itemSupplier;
    private final CustomBlockListener<T> listener;

    public CustomBlockManager(Class<T> customBlockClass, String fileName) {
        this.storage = Storage.map(BlockPosition.class, customBlockClass)
                .json(fileName, GsonProvider.builder().enableComplexMapKeySerialization().create())
                .container(HashMap::new);

        this.itemSupplier = null;
        this.listener = new CustomBlockListener<>(this);
    }

    public CustomBlockManager(Class<T> customBlockClass, String fileName, String itemId, boolean stackable, Function<T, ItemStack> supplier) {
        this.storage = Storage.map(BlockPosition.class, customBlockClass)
                .json(fileName, GsonProvider.builder().enableComplexMapKeySerialization().create())
                .container(HashMap::new);

        this.itemSupplier = new CustomBlockItemSupplier<>(itemId, stackable, supplier);
        this.listener = new CustomBlockListener<>(this);
    }

    public CustomBlockManager<T> init() {
        storage.get().forEach((blockPosition, customBlock) -> {
            customBlock.onLoad();
        });

        if (tickTask == null) {
            tickTask = Tasks.sync().timer(bukkitTask -> {
                storage.get().forEach((blockPosition, customBlock) -> {
                    customBlock.onTick();
                });
            }, 1L);
        }

        // Registering listener
        Bukkit.getPluginManager().registerEvents(this.listener, Provider.getPlugin());

        return this;
    }

    public CustomBlockManager<T> setAutoSave(long ticks) {
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

        storage.get().forEach((blockPosition, customBlock) -> {
            customBlock.onUnload();
        });

        save();
    }

    public void save() {
        storage.save();
    }

    public Collection<T> getCustomBlocks() {
        return storage.get().values();
    }

    public Optional<T> getCustomBlock(@Nonnull BlockPosition blockPosition) {
        return Optional.ofNullable(storage.get().get(blockPosition));
    }

    public T registerCustomBlock(T customBlock) {
        final BlockPosition blockPosition = customBlock.getBlockPosition();
        if (storage.get().containsKey(blockPosition)) throw new IllegalStateException("There is already a block at this location.");

        storage.get().put(blockPosition, customBlock);
        customBlock.onRegister();
        return customBlock;
    }

    public void unregisterCustomBlock(T customBlock) {
        if (!storage.get().containsValue(customBlock)) throw new IllegalStateException("Attempted to unregister an unregistered block.");

        customBlock.onUnregister();
        storage.get().remove(customBlock.getBlockPosition());
    }

    public CustomBlockItemSupplier<T> getItemSupplier() {
        return itemSupplier;
    }

    public CustomBlockListener<T> getListener() {
        return listener;
    }

}
