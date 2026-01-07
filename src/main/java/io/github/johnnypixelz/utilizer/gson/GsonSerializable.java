package io.github.johnnypixelz.utilizer.gson;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Nullable;

import org.jetbrains.annotations.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * An object which can be serialized to JSON.
 *
 * <p>Classes which implement this interface should also implement a static "deserialize" method,
 * accepting {@link JsonElement} as the only parameter.</p>
 */
public interface GsonSerializable {

    /**
     * Deserializes a JsonElement to a GsonSerializable object.
     *
     * @param clazz the GsonSerializable class
     * @param element the json element to deserialize
     * @param <T> the GsonSerializable type
     * @return the deserialized object
     * @throws IllegalStateException if the clazz does not have a deserialization method
     */
    @NotNull
    static <T extends GsonSerializable> T deserialize(@NotNull Class<T> clazz, @NotNull JsonElement element) {
        Method deserializeMethod = getDeserializeMethod(clazz);
        if (deserializeMethod == null) {
            throw new IllegalStateException("Class does not have a deserialize method accessible.");
        }

        try {
            //noinspection unchecked
            return (T) deserializeMethod.invoke(null, element);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserializes a JsonElement to a GsonSerializable object.
     *
     * @param clazz the GsonSerializable class
     * @param element the json element to deserialize
     * @return the deserialized object
     * @throws IllegalStateException if the clazz does not have a deserialization method
     */
    @NotNull
    static GsonSerializable deserializeRaw(@NotNull Class<?> clazz, @NotNull JsonElement element) {
        Class<? extends GsonSerializable> typeCastedClass = clazz.asSubclass(GsonSerializable.class);
        return deserialize(typeCastedClass, element);
    }

    /**
     * Gets the deserialization method for a given class.
     *
     * @param clazz the class
     * @return the deserialization method, if the class has one
     */
    @Nullable
    static Method getDeserializeMethod(@NotNull Class<?> clazz) {
        if (!GsonSerializable.class.isAssignableFrom(clazz)) {
            return null;
        }

        Method deserializeMethod;
        try {
            deserializeMethod = clazz.getDeclaredMethod("deserialize", JsonElement.class);
            deserializeMethod.setAccessible(true);
        } catch (Exception e) {
            return null;
        }

        if (!Modifier.isStatic(deserializeMethod.getModifiers())) {
            return null;
        }

        return deserializeMethod;
    }

    /**
     * Serializes the object to JSON
     *
     * @return a json form of this object
     */
    @NotNull
    JsonElement serialize();

}