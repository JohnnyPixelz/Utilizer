package io.github.johnnypixelz.utilizer.smartinvs.listener;

import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.InventoryManager;
import io.github.johnnypixelz.utilizer.Scheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.Consumer;

public class InventoryCloseListener extends InventoryListener<InventoryCloseEvent> {

    public InventoryCloseListener(Consumer<InventoryCloseEvent> consumer) {
        super(InventoryCloseEvent.class, onClose -> {
            Scheduler.syncDelayed(() -> {
                if (InventoryManager.getInventory((Player) onClose.getPlayer()).isPresent()) return;
                consumer.accept(onClose);
            }, 1L);
        });
    }
}