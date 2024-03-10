package io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem;

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

public class CustomBlockCustomItemManager<CB extends StatefulCustomBlock<CBD>, CBD extends CustomBlockData> {
    private final FileStorageContainer<Map<BlockPosition, CB>> storage;
    private final CustomBlockCustomItemListener<CB, CBD> listener;
    private final CustomBlockCustomItemSupplier<CB, CBD> itemSupplier;
    private final Class<CBD> customBlockDataType;

    private BukkitTask tickTask;
    private BukkitTask autoSaveTask;

    public CustomBlockCustomItemManager(Class<CB> customBlockType, Class<CBD> customBlockDataType, String fileName, NamespacedKey itemKey, boolean stackable, Function<CBD, ItemStack> supplier) {
        this.storage = Storage.map(BlockPosition.class, customBlockType)
                .json(fileName, GsonProvider.builder().enableComplexMapKeySerialization().create())
                .container(HashMap::new);

        this.customBlockDataType = customBlockDataType;
        this.itemSupplier = new CustomBlockCustomItemSupplier<CB, CBD>(itemKey, stackable, supplier, customBlockDataType);
        this.listener = new CustomBlockCustomItemListener<>(this);
    }

    public CustomBlockCustomItemManager<CB, CBD> init() {
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

    public CustomBlockCustomItemManager<CB, CBD> setAutoSave(long ticks) {
        storage.autoSave(ticks);
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

    public CustomBlockCustomItemSupplier<CB, CBD> getItemSupplier() {
        return itemSupplier;
    }

    public CustomBlockCustomItemListener<CB, CBD> getListener() {
        return listener;
    }

}
