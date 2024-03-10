package io.github.johnnypixelz.utilizer.features.customblocks;

import io.github.johnnypixelz.utilizer.serialize.world.BlockPosition;

public abstract class CustomBlock {
    private final BlockPosition blockPosition;

    public CustomBlock(BlockPosition blockPosition) {
        this.blockPosition = blockPosition;
    }

    public BlockPosition getBlockPosition() {
        return blockPosition;
    }

    public void onTick() {
    }

    public void onRegister() {
    }

    public void onUnregister() {
    }

    public void onLoad() {
    }

    public void onUnload() {
    }

}
