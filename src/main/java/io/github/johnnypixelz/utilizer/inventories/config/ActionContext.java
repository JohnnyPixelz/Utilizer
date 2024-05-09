package io.github.johnnypixelz.utilizer.inventories.config;

import io.github.johnnypixelz.utilizer.inventories.CustomInventory;
import org.bukkit.entity.Player;

public class ActionContext {
    private final CustomInventory inventory;
    private final Player player;
    private final String arguments;

    public ActionContext(CustomInventory inventory, Player player, String arguments) {
        this.inventory = inventory;
        this.player = player;
        this.arguments = arguments;
    }

    public CustomInventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public String getArguments() {
        return arguments;
    }

}
