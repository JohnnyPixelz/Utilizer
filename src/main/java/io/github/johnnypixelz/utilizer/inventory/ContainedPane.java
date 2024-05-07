package io.github.johnnypixelz.utilizer.inventory;

public class ContainedPane {
    private final Pane pane;
    private final int rawSlot;

    public ContainedPane(Pane pane, int rawSlot) {
        this.pane = pane;
        this.rawSlot = rawSlot;
    }

    public Pane getPane() {
        return pane;
    }

    public int getRawSlot() {
        return rawSlot;
    }

}
