package io.github.johnnypixelz.utilizer.features.customblocks;

import io.github.johnnypixelz.utilizer.event.BiStatefulEventEmitter;
import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.serialize.world.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Function;

public class CustomBlockListener<T extends CustomBlock> implements Listener {
    private final CustomBlockManager<T> manager;
    private boolean allowBreak; // Default: true
    private boolean dropWhenBroken; // Default: true
    private boolean placeable; // Default: true
    private boolean interactable; // Default: true
    private boolean movable; // Default: false
    private boolean explodable; // Default: false
    private final BiStatefulEventEmitter<BlockBreakEvent, T> breakEventEmitter;
    private final StatefulEventEmitter<BlockPlaceEvent> placeEventEmitter;
    private final BiStatefulEventEmitter<BlockPlaceEvent, T> postPlaceEventEmitter;
    private final BiStatefulEventEmitter<PlayerInteractEvent, T> leftClickInteractEventEmitter;
    private final BiStatefulEventEmitter<PlayerInteractEvent, T> rightClickInteractEventEmitter;
    private Function<BlockPlaceEvent, T> placeCustomBlockGenerator;

    public CustomBlockListener(CustomBlockManager<T> manager) {
        this.manager = manager;

        this.allowBreak = true;
        this.dropWhenBroken = true;
        this.placeable = true;
        this.interactable = true;
        this.movable = false;
        this.explodable = false;

        this.breakEventEmitter = new BiStatefulEventEmitter<>();
        this.placeEventEmitter = new StatefulEventEmitter<>();
        this.postPlaceEventEmitter = new BiStatefulEventEmitter<>();
        this.leftClickInteractEventEmitter = new BiStatefulEventEmitter<>();
        this.rightClickInteractEventEmitter = new BiStatefulEventEmitter<>();
    }

    public CustomBlockManager<T> getManager() {
        return manager;
    }

    public boolean isAllowBreak() {
        return allowBreak;
    }

    public CustomBlockListener<T> setAllowBreak(boolean allowBreak) {
        this.allowBreak = allowBreak;
        return this;
    }

    public boolean isDropWhenBroken() {
        return dropWhenBroken;
    }

    public CustomBlockListener<T> setDropWhenBroken(boolean dropWhenBroken) {
        this.dropWhenBroken = dropWhenBroken;
        return this;
    }

    public boolean isPlaceable() {
        return placeable;
    }

    public CustomBlockListener<T> setPlaceable(boolean placeable) {
        this.placeable = placeable;
        return this;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public CustomBlockListener<T> setInteractable(boolean interactable) {
        this.interactable = interactable;
        return this;
    }

    public boolean isMovable() {
        return movable;
    }

    public CustomBlockListener<T> setMovable(boolean movable) {
        this.movable = movable;
        return this;
    }

    public boolean isExplodable() {
        return explodable;
    }

    public CustomBlockListener<T> setExplodable(boolean explodable) {
        this.explodable = explodable;
        return this;
    }

    public Function<BlockPlaceEvent, T> getPlaceCustomBlockGenerator() {
        return placeCustomBlockGenerator;
    }

    public void setPlaceCustomBlockGenerator(Function<BlockPlaceEvent, T> placeCustomBlockGenerator) {
        this.placeCustomBlockGenerator = placeCustomBlockGenerator;
    }

    public BiStatefulEventEmitter<BlockBreakEvent, T> getBreakEventEmitter() {
        return breakEventEmitter;
    }

    public StatefulEventEmitter<BlockPlaceEvent> getPlaceEventEmitter() {
        return placeEventEmitter;
    }

    public BiStatefulEventEmitter<BlockPlaceEvent, T> getPostPlaceEventEmitter() {
        return postPlaceEventEmitter;
    }

    public BiStatefulEventEmitter<PlayerInteractEvent, T> getLeftClickInteractEventEmitter() {
        return leftClickInteractEventEmitter;
    }

    public BiStatefulEventEmitter<PlayerInteractEvent, T> getRightClickInteractEventEmitter() {
        return rightClickInteractEventEmitter;
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        final BlockPosition blockPosition = BlockPosition.of(block);
        final Optional<T> optionalCustomBlock = manager.getCustomBlock(blockPosition);
        if (optionalCustomBlock.isEmpty()) return;

        if (!allowBreak) {
            event.setCancelled(true);
        }

        final T customBlock = optionalCustomBlock.get();
        this.manager.unregisterCustomBlock(customBlock);

        this.breakEventEmitter.emit(event, customBlock);

        final CustomBlockItemSupplier<T> itemSupplier = this.manager.getItemSupplier();
        if (!event.isCancelled() && dropWhenBroken && itemSupplier != null && event.isDropItems()) {
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(block.getLocation(), itemSupplier.getItemStack(customBlock));
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent event) {
        final CustomBlockItemSupplier<T> itemSupplier = manager.getItemSupplier();
        if (itemSupplier == null) return;

        final ItemStack itemInHand = event.getItemInHand();
        final boolean similar = itemSupplier.isSimilar(itemInHand);
        if (!similar) return;

        if (!placeable) {
            event.setCancelled(true);
            return;
        }

        this.placeEventEmitter.emit(event);

        if (!event.isCancelled() && placeCustomBlockGenerator != null) {
            final T createdCustomBlock = placeCustomBlockGenerator.apply(event);
            if (createdCustomBlock != null) {
                this.manager.registerCustomBlock(createdCustomBlock);
                this.postPlaceEventEmitter.emit(event, createdCustomBlock);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) return;

        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        final BlockPosition blockPosition = BlockPosition.of(clickedBlock);
        final Optional<T> optionalCustomBlock = this.manager.getCustomBlock(blockPosition);
        if (optionalCustomBlock.isEmpty()) return;

        final T customBlock = optionalCustomBlock.get();

        if (!interactable) {
            event.setCancelled(true);
        }

        if (action == Action.LEFT_CLICK_BLOCK) {
            this.leftClickInteractEventEmitter.emit(event, customBlock);
        } else {
            this.rightClickInteractEventEmitter.emit(event, customBlock);
        }
    }

}
