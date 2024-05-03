package io.github.johnnypixelz.utilizer.inventory;

import io.github.johnnypixelz.utilizer.plugin.Logs;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.logging.Level;

public class InventoryManager {
    private static final Map<UUID, CustomInventory> inventories = new HashMap<>();

    static {
        Bukkit.getPluginManager().registerEvents(new InvListener(), Provider.getPlugin());
    }

    public static List<Player> getOpenedPlayers(CustomInventory inv) {
        List<Player> list = new ArrayList<>();

        InventoryManager.inventories.forEach((player, playerInv) -> {
            if (inv.equals(playerInv))
                list.add(Bukkit.getPlayer(player));
        });

        return list;
    }

    public static Optional<CustomInventory> getInventory(Player player) {
        return Optional.ofNullable(InventoryManager.inventories.get(player.getUniqueId()));
    }

    protected static void setInventory(Player player, CustomInventory inv) {

        // TODO handle old player inventory

        if (inv == null) {
            InventoryManager.inventories.remove(player.getUniqueId());
        } else {
            InventoryManager.inventories.put(player.getUniqueId(), inv);
        }
    }

    public static void handleInventoryOpenError(CustomInventory inventory, Player player, Exception exception) {
        inventory.close(player);

        Bukkit.getLogger().log(Level.SEVERE, "Error while opening CustomInventory:", exception);
    }

    public static void handleInventoryUpdateError(CustomInventory inventory, Player player, Exception exception) {
        inventory.close(player);

        Bukkit.getLogger().log(Level.SEVERE, "Error while updating CustomInventory:", exception);
    }

    @SuppressWarnings("unchecked")
    private static class InvListener implements Listener {

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClick(InventoryClickEvent event) {
            Player player = (Player) event.getWhoClicked();

            if (!inventories.containsKey(player.getUniqueId()))
                return;

            // Restrict putting items from the bottom inventory into the top inventory
            Inventory clickedInventory = event.getClickedInventory();
            if (clickedInventory == player.getOpenInventory().getBottomInventory()) {
                if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getAction() == InventoryAction.NOTHING && event.getClick() != ClickType.MIDDLE) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (clickedInventory == player.getOpenInventory().getTopInventory()) {
                event.setCancelled(true);

                if (event.getSlot() < 0) return;

                CustomInventory inv = inventories.get(player.getUniqueId());
                final int inventorySize = inv.getType().getSize();
                if (event.getSlot() >= inventorySize) return;

                inv.handleClick(event);

                player.updateInventory(); // TODO remove this and test
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryDrag(InventoryDragEvent event) {
            Player player = (Player) event.getWhoClicked();

            if (!inventories.containsKey(player.getUniqueId()))
                return;

            CustomInventory inv = inventories.get(player.getUniqueId());

            for (int slot : event.getRawSlots()) {
                if (slot >= player.getOpenInventory().getTopInventory().getSize())
                    continue;

                event.setCancelled(true);
                break;
            }

//            inv.getListeners().stream()
//                    .filter(listener -> listener.getType() == InventoryDragEvent.class)
//                    .forEach(listener -> ((InventoryListener<InventoryDragEvent>) listener).accept(event));
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryOpen(InventoryOpenEvent event) {
            Player player = (Player) event.getPlayer();

            if (!inventories.containsKey(player.getUniqueId()))
                return;

            CustomInventory inv = inventories.get(player.getUniqueId());
//            inv.onOpen(player);

            // TODO inventoryOpenEvent
//            inv.getListeners().stream()
//                    .filter(listener -> listener.getType() == InventoryOpenEvent.class)
//                    .forEach(listener -> ((InventoryListener<InventoryOpenEvent>) listener).accept(event));
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClose(InventoryCloseEvent event) {
            Player player = (Player) event.getPlayer();

            if (!inventories.containsKey(player.getUniqueId()))
                return;

            CustomInventory inv = inventories.get(player.getUniqueId());

            inventories.remove(player.getUniqueId());
            inv.onClose(player);

//            inv.getListeners().stream()
//                    .filter(listener -> listener.getType() == InventoryCloseEvent.class)
//                    .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener).accept(event));
//
//            if (inv.isCloseable() && inv.doesOpenParentOnClose()) {
//                inv.getParent().ifPresent(parent -> {
//                    Bukkit.getScheduler().runTask(Provider.getPlugin(), () -> parent.open(player));
//                });
//            }
//
//            if (inv.isCloseable()) {
//                event.getInventory().clear();
//
//                inventories.remove(player.getUniqueId());
//                contents.remove(player.getUniqueId());
//            } else {
//                Bukkit.getScheduler().runTask(Provider.getPlugin(), () -> player.openInventory(event.getInventory()));
//            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPlayerQuit(PlayerQuitEvent event) {
            Player player = event.getPlayer();

            if (!inventories.containsKey(player.getUniqueId()))
                return;

            CustomInventory inv = inventories.get(player.getUniqueId());

//            inv.getListeners().stream()
//                    .filter(listener -> listener.getType() == PlayerQuitEvent.class)
//                    .forEach(listener -> ((InventoryListener<PlayerQuitEvent>) listener).accept(event));

            inventories.remove(player.getUniqueId());
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPluginDisable(PluginDisableEvent event) {
            new HashMap<>(inventories).forEach((player, inv) -> {
//                inv.getListeners().stream()
//                        .filter(listener -> listener.getType() == PluginDisableEvent.class)
//                        .forEach(listener -> ((InventoryListener<PluginDisableEvent>) listener).accept(event));

                inv.close(Bukkit.getPlayer(player));
            });

            inventories.clear();
        }

    }

}
