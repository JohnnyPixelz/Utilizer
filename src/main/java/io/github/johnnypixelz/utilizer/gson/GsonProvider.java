package io.github.johnnypixelz.utilizer.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.github.johnnypixelz.utilizer.gson.typeadapters.BukkitSerializableAdapterFactory;
import io.github.johnnypixelz.utilizer.gson.typeadapters.GsonSerializableAdapterFactory;

import org.jetbrains.annotations.NotNull;

public class GsonProvider {
    private static final Gson STANDARD_GSON = new GsonBuilder()
            .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
            .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    private static final Gson PRETTY_PRINT_GSON = new GsonBuilder()
            .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
            .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private static final JsonParser PARSER = new JsonParser();

    @NotNull
    public static Gson standard() {
        return STANDARD_GSON;
    }

    @NotNull
    public static Gson prettyPrinting() {
        return PRETTY_PRINT_GSON;
    }

    @NotNull
    public static GsonBuilder builder() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
                .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
                .serializeNulls()
                .disableHtmlEscaping();
    }

    @NotNull
    public static JsonParser parser() {
        return PARSER;
    }

    private GsonProvider() {
    }
}
