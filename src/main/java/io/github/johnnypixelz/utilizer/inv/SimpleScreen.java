package io.github.johnnypixelz.utilizer.inv;

import io.github.johnnypixelz.utilizer.inv.elements.Button;
import io.github.johnnypixelz.utilizer.inv.elements.Panel;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import io.github.johnnypixelz.utilizer.itemstack.Skulls;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SimpleScreen extends Screen {
    private final ElementValue<Boolean> hasPerm = v(false);

    public SimpleScreen() {
        setTitle("&fSimple screen");
        setRows(3);
    }

    @Override
    public void render() {
        final ItemStack buttonStack = Items.create(Material.STICK, "Malakas");

        e(Panel.of(
                        ElementSize.of(3, 7),
                        List.of(Bukkit.getOfflinePlayers()),
                        offlinePlayer -> Skulls.getSkullFromUUID(offlinePlayer.getUniqueId())
                ),
                pos(1, 1)
        );

        e(Button.of(() -> buttonStack)
                        .onLeftClick(player -> hasPerm.setValue(!hasPerm.getValue()))
                        .visibility(hasPerm::getValue),
                pos(5, 4)
        );
    }

}
