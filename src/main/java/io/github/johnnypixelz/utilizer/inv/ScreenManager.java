package io.github.johnnypixelz.utilizer.inv;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class ScreenManager {
    private static final Map<Player, Screen> screens = new HashMap<>();

    static {
        Tasks.sync().timer(bukkitTask -> tick(), 1L);
        Bukkit.getPluginManager().registerEvents(new ScreenListener(), Provider.getPlugin());
    }

    public static Optional<Screen> getOpenScreen(Player player) {
        return Optional.ofNullable(screens.get(player));
    }

    public static List<Player> getViewers(Screen screen) {
        return screens.entrySet()
                .stream()
                .filter(playerScreenEntry -> playerScreenEntry.getValue() == screen)
                .map(Map.Entry::getKey)
                .toList();
    }

    static void registerViewer(Player viewer, Screen screen) {
        screens.put(viewer, screen);
    }

    private static void tick() {
        screens.forEach((uuid, screen) -> {

        });
    }

    public static class ScreenListener implements Listener {

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClick(InventoryClickEvent event) {
            Player player = (Player) event.getWhoClicked();

            if (!screens.containsKey(player))
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

                Screen inv = screens.get(player);

                if (row >= inv.getSize().getValue().getRows() || column >= inv.getSize().getValue().getColumns())
                    return;

                inv.handleClick(event);
//                inv.getListeners().stream()
//                        .filter(listener -> listener.getType() == InventoryClickEvent.class)
//                        .forEach(listener -> ((InventoryListener<InventoryClickEvent>) listener).accept(event));

//                contents.get(player.getUniqueId()).get(row, column).ifPresent(item -> item.run(event));

                player.updateInventory();
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        private void onInventoryClose(InventoryCloseEvent event) {
            if (!(event.getPlayer() instanceof Player player)) return;

            final Optional<Screen> screenOptional = getOpenScreen(player);
            if (screenOptional.isEmpty()) return;

            screens.remove(player);
        }

        @EventHandler(priority = EventPriority.LOW)
        private void onPlayerQuit(PlayerQuitEvent event) {
            final Player player = event.getPlayer();

            screens.remove(player);
        }

        @EventHandler(priority = EventPriority.LOW)
        private void onPluginDisable(PluginDisableEvent event) {
            for (Player player : new ArrayList<>(screens.keySet())) {
                player.closeInventory();
            }

            System.out.println("Closed all player inventories");
            System.out.println(screens);
        }

    }

}
