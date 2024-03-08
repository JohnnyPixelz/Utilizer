package io.github.johnnypixelz.utilizer.command;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandUtil {

    public static List<String> parseLabel(String label) {
        return Arrays.stream(label.split("\\|"))
                .map(String::trim)
                .toList();
    }

    public static <T> Optional<Class<? extends T>> findSuperclass(Class<T> type, Class<?> nestedClass) {
        while (nestedClass != Object.class) {
            if (type.isAssignableFrom(nestedClass)) {
                @SuppressWarnings("unchecked")
                Class<? extends T> subclass = (Class<? extends T>) nestedClass;
                return Optional.of(subclass);
            }

            nestedClass = nestedClass.getSuperclass();
        }

        return Optional.empty();
    }

}
