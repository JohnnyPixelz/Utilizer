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

    protected void onTick() {
    }

    protected void onRegister() {
    }

    protected void onUnregister() {
    }

    protected void onLoad() {
    }

    protected void onUnload() {
    }

}
