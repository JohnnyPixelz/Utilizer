package io.github.johnnypixelz.utilizer.inv.elements;

import io.github.johnnypixelz.utilizer.inv.ElementSize;
import io.github.johnnypixelz.utilizer.inv.ElementValue;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

public class Panel<T> extends Box {

    public static <T> Panel<T> of(ElementSize elementSize, List<T> elements, Function<T, ItemStack> mapper) {
        return new Panel<>(elementSize, elements, mapper);
    }

    private final ElementValue<List<T>> elements;
    private final ElementValue<Function<T, ItemStack>> mapper;
    private final ElementValue<Integer> page;

    protected Panel(ElementSize elementSize, List<T> elements, Function<T, ItemStack> mapper) {
        super(elementSize);
        this.elements = v(elements);
        this.mapper = v(mapper);
        this.page = v(1);
    }

    public Panel<T> setElements(List<T> elements) {
        this.elements.setValue(elements);
        return this;
    }

    public Panel<T> setMapper(Function<T, ItemStack> mapper) {
        this.mapper.setValue(mapper);
        return this;
    }

    public Panel<T> setPage(int page) {
        this.page.setValue(page);
        return this;
    }

    @Override
    public void render() {

    }

}
