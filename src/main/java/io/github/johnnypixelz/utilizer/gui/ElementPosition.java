package io.github.johnnypixelz.utilizer.gui;

import io.github.johnnypixelz.utilizer.config.Parse;

public class ElementPosition {

    public static ElementPosition of(int row, int column) {
        return new ElementPosition(
                Parse.constrain(0, 5, row),
                Parse.constrain(0, 8, column)
        );
    }

    private final int row;
    private final int column;

    private ElementPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

}
