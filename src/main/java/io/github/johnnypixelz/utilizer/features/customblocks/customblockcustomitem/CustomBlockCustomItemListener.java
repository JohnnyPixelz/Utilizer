package io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem;

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

public class CustomBlockCustomItemListener<CB extends StatefulCustomBlock<CBD>, CBD extends CustomBlockData> implements Listener {
    private final CustomBlockCustomItemManager<CB, CBD> manager;
    private boolean allowBreak; // Default: true
    private boolean dropWhenBroken; // Default: true
    private boolean placeable; // Default: true
    private boolean interactable; // Default: true
    private boolean movable; // Default: false
    private boolean explodable; // Default: false
    private final BiStatefulEventEmitter<BlockBreakEvent, CB> breakEventEmitter;
    private final StatefulEventEmitter<BlockPlaceEvent> placeEventEmitter;
    private final BiStatefulEventEmitter<BlockPlaceEvent, CB> postPlaceEventEmitter;
    private final BiStatefulEventEmitter<PlayerInteractEvent, CB> leftClickInteractEventEmitter;
    private final BiStatefulEventEmitter<PlayerInteractEvent, CB> rightClickInteractEventEmitter;
    private CustomBlockCustomItemGenerator<CB, CBD> customBlockGenerator;

    public CustomBlockCustomItemListener(CustomBlockCustomItemManager<CB, CBD> manager) {
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

    public CustomBlockCustomItemManager<CB, CBD> getManager() {
        return manager;
    }

    public boolean isAllowBreak() {
        return allowBreak;
    }

    public CustomBlockCustomItemListener<CB, CBD> setAllowBreak(boolean allowBreak) {
        this.allowBreak = allowBreak;
        return this;
    }

    public boolean isDropWhenBroken() {
        return dropWhenBroken;
    }

    public CustomBlockCustomItemListener<CB, CBD> setDropWhenBroken(boolean dropWhenBroken) {
        this.dropWhenBroken = dropWhenBroken;
        return this;
    }

    public boolean isPlaceable() {
        return placeable;
    }

    public CustomBlockCustomItemListener<CB, CBD> setPlaceable(boolean placeable) {
        this.placeable = placeable;
        return this;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public CustomBlockCustomItemListener<CB, CBD> setInteractable(boolean interactable) {
        this.interactable = interactable;
        return this;
    }

    public boolean isMovable() {
        return movable;
    }

    public CustomBlockCustomItemListener<CB, CBD> setMovable(boolean movable) {
        this.movable = movable;
        return this;
    }

    public boolean isExplodable() {
        return explodable;
    }

    public CustomBlockCustomItemListener<CB, CBD> setExplodable(boolean explodable) {
        this.explodable = explodable;
        return this;
    }

    public CustomBlockCustomItemGenerator<CB, CBD> getCustomBlockGenerator() {
        return customBlockGenerator;
    }

    public void setCustomBlockGenerator(CustomBlockCustomItemGenerator<CB, CBD> placeCustomBlockGenerator) {
        this.customBlockGenerator = placeCustomBlockGenerator;
    }

    public BiStatefulEventEmitter<BlockBreakEvent, CB> getBreakEventEmitter() {
        return breakEventEmitter;
    }

    public StatefulEventEmitter<BlockPlaceEvent> getPlaceEventEmitter() {
        return placeEventEmitter;
    }

    public BiStatefulEventEmitter<BlockPlaceEvent, CB> getPostPlaceEventEmitter() {
        return postPlaceEventEmitter;
    }

    public BiStatefulEventEmitter<PlayerInteractEvent, CB> getLeftClickInteractEventEmitter() {
        return leftClickInteractEventEmitter;
    }

    public BiStatefulEventEmitter<PlayerInteractEvent, CB> getRightClickInteractEventEmitter() {
        return rightClickInteractEventEmitter;
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        final BlockPosition blockPosition = BlockPosition.of(block);
        final Optional<CB> optionalCustomBlock = manager.getCustomBlock(blockPosition);
        if (optionalCustomBlock.isEmpty()) return;

        if (!allowBreak) {
            event.setCancelled(true);
        }

        final CB customBlock = optionalCustomBlock.get();
        this.manager.unregisterCustomBlock(customBlock);

        this.breakEventEmitter.emit(event, customBlock);

        final CustomBlockCustomItemSupplier<CB, CBD> itemSupplier = this.manager.getItemSupplier();
        if (!event.isCancelled() && dropWhenBroken && itemSupplier != null && event.isDropItems()) {
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(block.getLocation(), itemSupplier.getItemStack(customBlock.getData()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent event) {
        final CustomBlockCustomItemSupplier<CB, CBD> itemSupplier = manager.getItemSupplier();
        if (itemSupplier == null) return;

        final ItemStack itemInHand = event.getItemInHand();
        final boolean similar = itemSupplier.isSameType(itemInHand);
        if (!similar) return;

        if (!placeable) {
            event.setCancelled(true);
            return;
        }

        this.placeEventEmitter.emit(event);

        if (!event.isCancelled() && customBlockGenerator != null) {
            final Optional<CBD> optionalCustomBlockData = this.manager.getItemSupplier().getCustomData(itemInHand);
            if (optionalCustomBlockData.isEmpty()) return;
            final CBD customBlockData = optionalCustomBlockData.get();

            final CB createdCustomBlock = customBlockGenerator.apply(event, BlockPosition.of(event.getBlockPlaced()), customBlockData, itemInHand);
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
        final Optional<CB> optionalCustomBlock = this.manager.getCustomBlock(blockPosition);
        if (optionalCustomBlock.isEmpty()) return;

        final CB customBlock = optionalCustomBlock.get();

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
