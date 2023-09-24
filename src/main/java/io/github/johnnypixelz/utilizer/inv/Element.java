package io.github.johnnypixelz.utilizer.inv;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class Element {
    private Element parentElement;
    private final ElementValueManager elementValueManager;
    private Supplier<Boolean> visibilitySupplier;

    public Element(Element parentElement) {
        this.parentElement = parentElement;
        this.elementValueManager = new ElementValueManager();
        this.visibilitySupplier = () -> true;

        elementValueManager.setOnUpdate(() -> {
            if (getParentElement().isEmpty() && !(this instanceof Screen)) return;

            render();
        });
    }

    public Element() {
        this(null);
    }

    public void setParentElement(Element parentElement) {
        this.parentElement = parentElement;
    }

    public <T> ElementValue<T> v(T defaultValue) {
        final ElementValue<T> elementValue = ElementValue.of(defaultValue);
        this.elementValueManager.addValue(elementValue);

        return elementValue;
    }

    public ElementValueManager getElementValueManager() {
        return elementValueManager;
    }

    public boolean isVisible() {
        return visibilitySupplier.get();
    }

    public Element visibility(Supplier<Boolean> supplier) {
        this.visibilitySupplier = supplier;
        return this;
    }

    public Optional<Element> getParentElement() {
        return Optional.ofNullable(parentElement);
    }

    public void render() {
    }

    protected void handleClick(InventoryClickEvent event) {
    }

    protected Inventory getInventory() {
        Element parent = this;

        while (parent != null) {
            if (parent instanceof Screen screen) {
                return screen.getInventory();
            }

            parent = parent.getParentElement().orElse(null);
        }

        throw new IllegalStateException("This element has no available inventory");
    }

    protected ElementPosition getPosition() {
        if (parentElement == null) throw new IllegalStateException("This element has no position");

        if (parentElement instanceof SizedElement sizedElement) {
            return sizedElement.getContainedElements()
                    .stream()
                    .filter(containedElement -> containedElement.getElement() == this)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("This element has no position"))
                    .getElementPosition();
        }

        return parentElement.getPosition();
    }

    protected void setItem(ItemStack item) {
        final Inventory inventory = getInventory();
        final ElementPosition position = getPosition();

        inventory.setItem((position.getRow() * 9) + position.getColumn(), item);
    }

}
