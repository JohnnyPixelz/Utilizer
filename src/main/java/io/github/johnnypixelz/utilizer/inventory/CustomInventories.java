package io.github.johnnypixelz.utilizer.inventory;

import com.google.common.base.Converter;
import com.google.common.collect.ImmutableList;
import io.github.johnnypixelz.utilizer.inventory.inventories.PickerInventory;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomInventories {

    public static <T> PickerInventory<T> pick(List<T> items, Function<T, ItemStack> converter, Consumer<T> onPick) {
        return new PickerInventory.PickerInventoryBuilder<T>()
                .setItems(items)
                .setConverter(converter)
                .setOnPick(onPick)
                .build();
    }

    public static PickerInventory<Player> pickPlayer(Consumer<Player> onPick) {
        return pickPlayer(player -> {
            return Items.edit(Material.PLAYER_HEAD)
                    .setDisplayName("&f%name%")
                    .addLore("&7Left Click to select player")
                    .map("%name%", player.getName())
                    .meta(SkullMeta.class, skullMeta -> skullMeta.setOwningPlayer(player))
                    .getItem();
        }, onPick);
    }

    public static PickerInventory<Player> pickPlayer(Function<Player, ItemStack> converter, Consumer<Player> onPick) {
        return pick(ImmutableList.copyOf(Bukkit.getOnlinePlayers()), converter, onPick);
    }

}
