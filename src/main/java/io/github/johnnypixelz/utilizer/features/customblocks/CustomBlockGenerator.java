package io.github.johnnypixelz.utilizer.features.customblocks;

import io.github.johnnypixelz.utilizer.serialize.world.BlockPosition;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface CustomBlockGenerator<T> {

    T apply(BlockPlaceEvent event, BlockPosition blockPosition, ItemStack itemStack);

}
