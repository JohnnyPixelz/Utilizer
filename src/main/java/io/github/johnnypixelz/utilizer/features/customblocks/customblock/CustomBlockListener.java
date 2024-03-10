package io.github.johnnypixelz.utilizer.features.customblocks.customblock;

import io.github.johnnypixelz.utilizer.event.BiStatefulEventEmitter;
import io.github.johnnypixelz.utilizer.features.customblocks.CustomBlock;
import io.github.johnnypixelz.utilizer.features.customblocks.CustomBlockGenerator;
import io.github.johnnypixelz.utilizer.serialize.world.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class CustomBlockListener<T extends CustomBlock> implements Listener {
    private final CustomBlockManager<T> manager;
    private boolean allowBreak; // Default: true
    private boolean interactable; // Default: true
    private boolean movable; // Default: false
    private boolean explodable; // Default: false
    private final BiStatefulEventEmitter<BlockBreakEvent, T> breakEventEmitter;
    private final BiStatefulEventEmitter<PlayerInteractEvent, T> leftClickInteractEventEmitter;
    private final BiStatefulEventEmitter<PlayerInteractEvent, T> rightClickInteractEventEmitter;
    private CustomBlockGenerator<T> customBlockGenerator;

    public CustomBlockListener(CustomBlockManager<T> manager) {
        this.manager = manager;

        this.allowBreak = true;
        this.interactable = true;
        this.movable = false;
        this.explodable = false;

        this.breakEventEmitter = new BiStatefulEventEmitter<>();
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

    public CustomBlockGenerator<T> getPlaceCustomBlockGenerator() {
        return customBlockGenerator;
    }

    public void setPlaceCustomBlockGenerator(CustomBlockGenerator<T> placeCustomBlockGenerator) {
        this.customBlockGenerator = placeCustomBlockGenerator;
    }

    public BiStatefulEventEmitter<BlockBreakEvent, T> getBreakEventEmitter() {
        return breakEventEmitter;
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
