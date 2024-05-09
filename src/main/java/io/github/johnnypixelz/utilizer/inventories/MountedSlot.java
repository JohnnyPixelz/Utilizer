package io.github.johnnypixelz.utilizer.inventories;

public class MountedSlot {
    private final ContentHolder contentHolder;
    private final int rawSlot;

    public MountedSlot(ContentHolder contentHolder, int rawSlot) {
        this.contentHolder = contentHolder;
        this.rawSlot = rawSlot;
    }

    public ContentHolder getContentHolder() {
        return contentHolder;
    }

    public int getRawSlot() {
        return rawSlot;
    }

}
