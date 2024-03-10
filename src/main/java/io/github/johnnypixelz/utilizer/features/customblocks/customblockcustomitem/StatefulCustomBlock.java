package io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem;

import io.github.johnnypixelz.utilizer.features.customblocks.CustomBlock;
import io.github.johnnypixelz.utilizer.serialize.world.BlockPosition;

public abstract class StatefulCustomBlock<CBD extends CustomBlockData> extends CustomBlock {
    private CBD customBlockData;

    public StatefulCustomBlock(BlockPosition blockPosition, CBD customBlockData) {
        super(blockPosition);
        this.customBlockData = customBlockData;
    }

    public CBD getData() {
        return customBlockData;
    }

}
