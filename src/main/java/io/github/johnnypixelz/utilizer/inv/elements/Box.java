package io.github.johnnypixelz.utilizer.inv.elements;

import io.github.johnnypixelz.utilizer.inv.ElementSize;
import io.github.johnnypixelz.utilizer.inv.SizedElement;

public class Box extends SizedElement {

    public static Box of(ElementSize size) {
        return new Box(size);
    }

    protected Box(ElementSize elementSize) {
        super(elementSize);
    }

}
