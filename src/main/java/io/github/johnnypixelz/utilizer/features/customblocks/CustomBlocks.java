package io.github.johnnypixelz.utilizer.features.customblocks;

import io.github.johnnypixelz.utilizer.features.customblocks.builder.CustomBlockBuilderFileStep;
import io.github.johnnypixelz.utilizer.features.customblocks.builder.CustomBlockWithDataBuilderFileStep;
import io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem.CustomBlockData;
import io.github.johnnypixelz.utilizer.features.customblocks.customblockcustomitem.StatefulCustomBlock;

public class CustomBlocks {

    public static <CB extends CustomBlock> CustomBlockBuilderFileStep<CB> create(Class<CB> customBlockType) {
        return new CustomBlockBuilderFileStep<>(customBlockType);
    }

    public static <CB extends StatefulCustomBlock<CBD>, CBD extends CustomBlockData> CustomBlockWithDataBuilderFileStep<CB, CBD> create(Class<CB> customBlockType, Class<CBD> customBlockDataType) {
        return new CustomBlockWithDataBuilderFileStep<>(customBlockType, customBlockDataType);
    }

}
