package io.github.johnnypixelz.utilizer;

import io.github.johnnypixelz.utilizer.gson.GsonProvider;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MainTest {

    @Test
    public void testColor() {
        assertEquals(Colors.color("&7Test"), "§7Test");
    }

    @Test
    public void testJsonString() {
        String key = "123abc";

        final String json = GsonProvider.standard().toJson(key, String.class);
        final String fromJson = GsonProvider.standard().fromJson(json, String.class);

        assertEquals(fromJson, key);
    }

    @Test
    public void testJsonUUID() {
        UUID uuid = UUID.randomUUID();

        final String json = GsonProvider.standard().toJson(uuid, UUID.class);
        final UUID fromJson = GsonProvider.standard().fromJson(json, UUID.class);

        assertEquals(uuid, fromJson);
    }

}
