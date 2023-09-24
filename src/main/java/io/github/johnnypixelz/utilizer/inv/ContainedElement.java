package io.github.johnnypixelz.utilizer.inv;

public class ContainedElement {

    public static ContainedElement of(Element element, ElementPosition elementPosition) {
        return new ContainedElement(
                element,
                 elementPosition
        );
    }

    private final Element element;
    private final ElementPosition elementPosition;

    private ContainedElement(Element element, ElementPosition elementPosition) {
        this.element = element;
        this.elementPosition = elementPosition;
    }

    public Element getElement() {
        return element;
    }

    public ElementPosition getElementPosition() {
        return elementPosition;
    }

}
