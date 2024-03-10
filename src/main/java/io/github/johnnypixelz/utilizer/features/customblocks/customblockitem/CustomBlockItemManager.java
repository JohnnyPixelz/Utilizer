package io.github.johnnypixelz.utilizer.features.customblocks.customblockitem;

import io.github.johnnypixelz.utilizer.features.customblocks.CustomBlock;
import io.github.johnnypixelz.utilizer.file.storage.Storage;
import io.github.johnnypixelz.utilizer.file.storage.container.file.FileStorageContainer;
import io.github.johnnypixelz.utilizer.gson.GsonProvider;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import io.github.johnnypixelz.utilizer.serialize.world.BlockPosition;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class CustomBlockItemManager<CB extends CustomBlock> {
    private final FileStorageContainer<Map<BlockPosition, CB>> storage;
    private final CustomBlockItemListener<CB> listener;
    private final CustomBlockItemSupplier<CB> itemSupplier;

    private BukkitTask tickTask;
    private BukkitTask autoSaveTask;

    public CustomBlockItemManager(Class<CB> customBlockType, String fileName, NamespacedKey itemKey, boolean stackable, Function<CB, ItemStack> supplier) {
        this.storage = Storage.map(BlockPosition.class, customBlockType)
                .json(fileName, GsonProvider.builder().enableComplexMapKeySerialization().create())
                .container(HashMap::new);

        this.itemSupplier = new CustomBlockItemSupplier<>(itemKey, stackable, supplier);
        this.listener = new CustomBlockItemListener<>(this);
    }

    public CustomBlockItemManager<CB> init() {
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

    public CustomBlockItemManager<CB> setAutoSave(long ticks) {
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

    public Collection<CB> getCustomBlocks() {
        return storage.get().values();
    }

    public Optional<CB> getCustomBlock(@Nonnull BlockPosition blockPosition) {
        return Optional.ofNullable(storage.get().get(blockPosition));
    }

    public CB registerCustomBlock(CB customBlock) {
        final BlockPosition blockPosition = customBlock.getBlockPosition();
        if (storage.get().containsKey(blockPosition))
            throw new IllegalStateException("There is already a block at this location.");

        storage.get().put(blockPosition, customBlock);
        customBlock.onRegister();
        return customBlock;
    }

    public void unregisterCustomBlock(CB customBlock) {
        if (!storage.get().containsValue(customBlock))
            throw new IllegalStateException("Attempted to unregister an unregistered block.");

        customBlock.onUnregister();
        storage.get().remove(customBlock.getBlockPosition());
    }

    public CustomBlockItemSupplier<CB> getItemSupplier() {
        return itemSupplier;
    }

    public CustomBlockItemListener<CB> getListener() {
        return listener;
    }

}
