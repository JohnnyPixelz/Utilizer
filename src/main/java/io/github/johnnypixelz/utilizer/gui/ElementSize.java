package io.github.johnnypixelz.utilizer.gui;

import io.github.johnnypixelz.utilizer.config.Parse;

public class ElementSize {

    public static ElementSize of(int rows, int columns) {
        return new ElementSize(
                Parse.constrain(0, 5, rows),
                Parse.constrain(0, 8, columns)
        );
    }

    private final int rows;
    private final int columns;

    private ElementSize(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

}
