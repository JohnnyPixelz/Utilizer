package io.github.johnnypixelz.utilizer.inventory;

import io.github.johnnypixelz.utilizer.inventory.content.InventoryContents;
import io.github.johnnypixelz.utilizer.inventory.openers.ChestInventoryOpener;
import io.github.johnnypixelz.utilizer.inventory.openers.InventoryOpener;
import io.github.johnnypixelz.utilizer.inventory.openers.SpecialInventoryOpener;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

public class InventoryManager {
    private static final Map<UUID, CustomInventory> inventories = new HashMap<>();
    private static final Map<UUID, InventoryContents> contents = new HashMap<>();

    private static final List<InventoryOpener> defaultOpeners = Arrays.asList(
            new ChestInventoryOpener(),
            new SpecialInventoryOpener()
    );
    private static final List<InventoryOpener> openers = new ArrayList<>();

    static {
        new InvTask().runTaskTimer(Provider.getPlugin(), 1, 1);
        Bukkit.getPluginManager().registerEvents(new InvListener(), Provider.getPlugin());
    }

    public static Optional<InventoryOpener> findOpener(InventoryType type) {
        Optional<InventoryOpener> opInv = openers.stream()
                .filter(opener -> opener.supports(type))
                .findAny();

        if (opInv.isEmpty()) {
            opInv = defaultOpeners.stream()
                    .filter(opener -> opener.supports(type))
                    .findAny();
        }

        return opInv;
    }

    public static void registerOpeners(InventoryOpener... openers) {
        InventoryManager.openers.addAll(Arrays.asList(openers));
    }

    public static List<Player> getOpenedPlayers(CustomInventory inv) {
        List<Player> list = new ArrayList<>();

        InventoryManager.inventories.forEach((player, playerInv) -> {
            if (inv.equals(playerInv))
                list.add(Bukkit.getPlayer(player));
        });

        return list;
    }

    public static Optional<CustomInventory> getInventory(Player p) {
        return Optional.ofNullable(InventoryManager.inventories.get(p.getUniqueId()));
    }

    protected static void setInventory(Player p, CustomInventory inv) {
        if (inv == null)
            InventoryManager.inventories.remove(p.getUniqueId());
        else
            InventoryManager.inventories.put(p.getUniqueId(), inv);
    }

    public static Optional<InventoryContents> getContents(Player p) {
        return Optional.ofNullable(InventoryManager.contents.get(p.getUniqueId()));
    }

    protected static void setContents(Player p, InventoryContents contents) {
        if (contents == null)
            InventoryManager.contents.remove(p.getUniqueId());
        else
            InventoryManager.contents.put(p.getUniqueId(), contents);
    }

    public static void handleInventoryOpenError(CustomInventory inventory, Player player, Exception exception) {
        inventory.close(player);

        Bukkit.getLogger().log(Level.SEVERE, "Error while opening SmartInventory:", exception);
    }

    public static void handleInventoryUpdateError(CustomInventory inventory, Player player, Exception exception) {
        inventory.close(player);

        Bukkit.getLogger().log(Level.SEVERE, "Error while updating SmartInventory:", exception);
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

                int row = event.getSlot() / 9;
                int column = event.getSlot() % 9;

                if (row < 0 || column < 0)
                    return;

                CustomInventory inv = inventories.get(player.getUniqueId());

                if (row >= inv.getRows() || column >= inv.getColumns())
                    return;

                inv.getListeners().stream()
                        .filter(listener -> listener.getType() == InventoryClickEvent.class)
                        .forEach(listener -> ((InventoryListener<InventoryClickEvent>) listener).accept(event));

                contents.get(player.getUniqueId()).get(row, column).ifPresent(item -> item.run(event));

                player.updateInventory();
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

            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == InventoryDragEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryDragEvent>) listener).accept(event));
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryOpen(InventoryOpenEvent event) {
            Player player = (Player) event.getPlayer();

            if (!inventories.containsKey(player.getUniqueId()))
                return;

            CustomInventory inv = inventories.get(player.getUniqueId());

            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == InventoryOpenEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryOpenEvent>) listener).accept(event));
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClose(InventoryCloseEvent event) {
            Player player = (Player) event.getPlayer();

            if (!inventories.containsKey(player.getUniqueId()))
                return;

            CustomInventory inv = inventories.get(player.getUniqueId());

            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener).accept(event));

            if (inv.isCloseable() && inv.doesOpenParentOnClose()) {
                inv.getParent().ifPresent(parent -> {
                    Bukkit.getScheduler().runTask(Provider.getPlugin(), () -> parent.open(player));
                });
            }

            if (inv.isCloseable()) {
                event.getInventory().clear();

                inventories.remove(player.getUniqueId());
                contents.remove(player.getUniqueId());
            } else {
                Bukkit.getScheduler().runTask(Provider.getPlugin(), () -> player.openInventory(event.getInventory()));
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPlayerQuit(PlayerQuitEvent event) {
            Player player = event.getPlayer();

            if (!inventories.containsKey(player.getUniqueId()))
                return;

            CustomInventory inv = inventories.get(player.getUniqueId());

            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == PlayerQuitEvent.class)
                    .forEach(listener -> ((InventoryListener<PlayerQuitEvent>) listener).accept(event));

            inventories.remove(player.getUniqueId());
            contents.remove(player.getUniqueId());
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPluginDisable(PluginDisableEvent event) {
            new HashMap<>(inventories).forEach((player, inv) -> {
                inv.getListeners().stream()
                        .filter(listener -> listener.getType() == PluginDisableEvent.class)
                        .forEach(listener -> ((InventoryListener<PluginDisableEvent>) listener).accept(event));

                inv.close(Bukkit.getPlayer(player));
            });

            inventories.clear();
            contents.clear();
        }

    }

    private static class InvTask extends BukkitRunnable {

        @Override
        public void run() {
            new HashMap<>(inventories).forEach((uuid, inv) -> {
                Player player = Bukkit.getPlayer(uuid);

                try {
                    inv.getProvider().update(player, contents.get(uuid));
                } catch (Exception ex) {
                    handleInventoryUpdateError(inv, player, ex);
                }
            });
        }

    }

}
