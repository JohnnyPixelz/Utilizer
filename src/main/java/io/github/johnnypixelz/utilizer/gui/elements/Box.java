package io.github.johnnypixelz.utilizer.gui.elements;

import io.github.johnnypixelz.utilizer.gui.ElementSize;
import io.github.johnnypixelz.utilizer.gui.SizedElement;

public class Box extends SizedElement {

    public static Box of(ElementSize size) {
        return new Box(size);
    }

    protected Box(ElementSize elementSize) {
        super(elementSize);
    }

}
