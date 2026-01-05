package io.github.johnnypixelz.utilizer.features.customblocks.data;

/**
 * Interface for custom blocks that hold serializable data.
 * <p>
 * Implement this interface on your CustomBlock subclass to enable
 * data serialization to/from items when blocks are placed or broken.
 * <p>
 * Example:
 * <pre>{@code
 * public class MyDataBlock extends CustomBlock implements BlockDataHolder<MyBlockData> {
 *     private final MyBlockData data;
 *
 *     public MyDataBlock(BlockPosition pos, MyBlockData data) {
 *         super(pos);
 *         this.data = data;
 *     }
 *
 *     @Override
 *     public MyBlockData getData() {
 *         return data;
 *     }
 *
 *     @Override
 *     public Class<MyBlockData> getDataType() {
 *         return MyBlockData.class;
 *     }
 * }
 * }</pre>
 *
 * @param <T> the type of data held by this block
 */
public interface BlockDataHolder<T> {

    /**
     * Returns the data associated with this block.
     * This data will be serialized to items when the block is dropped.
     *
     * @return the block's data, may be null
     */
    T getData();

    /**
     * Returns the class type of the data for serialization purposes.
     *
     * @return the data class type
     */
    Class<T> getDataType();

}
