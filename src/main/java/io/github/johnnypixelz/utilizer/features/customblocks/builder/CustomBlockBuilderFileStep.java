package io.github.johnnypixelz.utilizer.features.customblocks.builder;

import io.github.johnnypixelz.utilizer.features.customblocks.CustomBlock;

public class CustomBlockBuilderFileStep<CB extends CustomBlock> {
    private final Class<CB> customBlockType;

    public CustomBlockBuilderFileStep(Class<CB> customBlockType) {
        this.customBlockType = customBlockType;
    }

    public CustomBlockBuilderDetailStep<CB> json(String fileName) {
        return new CustomBlockBuilderDetailStep<>(customBlockType, fileName);
    }

}
