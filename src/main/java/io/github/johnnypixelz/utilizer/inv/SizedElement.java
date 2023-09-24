package io.github.johnnypixelz.utilizer.inv;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class SizedElement extends Element {
    protected ElementValue<ElementSize> size;
    private final List<ContainedElement> containedElements;

    protected SizedElement(ElementSize elementSize) {
        this.size = v(elementSize);
        this.containedElements = new ArrayList<>();
    }

    public ElementValue<ElementSize> getSize() {
        return size;
    }

    public List<ContainedElement> getContainedElements() {
        return containedElements;
    }

    public SizedElement e(Element element, ElementPosition elementPosition, ElementValue<?>... elementValues) {
        element.setParentElement(this);

        final ContainedElement containedElement = ContainedElement.of(element, elementPosition);
        containedElements.add(containedElement);

        for (ElementValue<?> elementValue : elementValues) {
            element.getElementValueManager().addValue(elementValue);
        }

        element.render();
        return this;
    }

    protected ElementPosition pos(int row, int column) {
        return ElementPosition.of(row, column);
    }

    public List<ContainedElement> getContainedElementsAt(int row, int column) {
        return containedElements.stream()
                .filter(containedElement -> {
                    final Element element = containedElement.getElement();
                    final ElementPosition position = containedElement.getElementPosition();

                    if (element instanceof SizedElement sizedElement) {
                        final ElementSize size = sizedElement.getSize().getValue();
                        return row >= position.getRow()
                                && row <= position.getRow() + size.getRows()
                                && column >= position.getColumn()
                                && column <= position.getColumn() + size.getColumns();
                    } else {
                        return position.getRow() == row && position.getColumn() == column;
                    }
                })
                .toList();
    }

    @Override
    protected void handleClick(InventoryClickEvent event) {
        int row = event.getSlot() / 9;
        int column = event.getSlot() % 9;

        for (ContainedElement containedElement : getContainedElementsAt(row, column)) {
            containedElement.getElement().handleClick(event);
        }
    }

    protected void setItem(int row, int column, ItemStack item) {
        getInventory().setItem(row * column, item);
    }

}
