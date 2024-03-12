package io.github.johnnypixelz.utilizer.features.customblocks.builder;

import io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem.CustomBlockData;
import io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem.StatefulCustomBlock;

public class CustomBlockWithDataBuilderFileStep<CB extends StatefulCustomBlock<CBD>, CBD extends CustomBlockData> {
    private final Class<CB> customBlockType;
    private final Class<CBD> customBlockDataType;

    public CustomBlockWithDataBuilderFileStep(Class<CB> customBlockType, Class<CBD> customBlockDataType) {
        this.customBlockType = customBlockType;
        this.customBlockDataType = customBlockDataType;
    }

    public CustomBlockWithDataBuilderItemStep<CB, CBD> json(String fileName) {
        return new CustomBlockWithDataBuilderItemStep<>(customBlockType, customBlockDataType, fileName);
    }

}
