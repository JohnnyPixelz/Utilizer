package io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem;

import io.github.johnnypixelz.utilizer.serialize.world.BlockPosition;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface CustomBlockCustomItemGenerator<CB extends StatefulCustomBlock<CBD>, CBD extends CustomBlockData> {

    CB apply(BlockPlaceEvent event, BlockPosition blockPosition, CBD customBlockData, ItemStack itemStack);

}
