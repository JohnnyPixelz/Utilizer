package io.github.johnnypixelz.utilizer.features.customblocks;

import io.github.johnnypixelz.utilizer.features.customblocks.builder.CustomBlockBuilderFileStep;
import io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem.CustomBlockData;

public class CustomBlocks {

    public static <CB extends CustomBlock> CustomBlockBuilderFileStep<CB> create(Class<CB> customBlockType) {
        return new CustomBlockBuilderFileStep<>(customBlockType);
    }

    public static <CB extends CustomBlock, CBD extends CustomBlockData> void create(Class<CB> customBlockType, Class<CBD> customBlockDataType) {

    }

}
