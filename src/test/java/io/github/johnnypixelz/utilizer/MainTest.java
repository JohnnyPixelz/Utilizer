package io.github.johnnypixelz.utilizer;

import io.github.johnnypixelz.utilizer.gson.GsonProvider;
import io.github.johnnypixelz.utilizer.text.Colors;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MainTest {

    @Test
    public void testColorLegacy() {
        // Legacy & codes
        assertEquals("§7Test", Colors.color("&7Test"));
        assertEquals("§a§lBold Green", Colors.color("&a&lBold Green"));
    }

    @Test
    public void testColorHex() {
        // Hex &#FFFFFF format - uses §x§R§R§G§G§B§B format (case insensitive)
        String result = Colors.color("&#FF5555Red").toLowerCase();
        assert result.startsWith("§x§f§f§5§5§5§5") : "Expected hex color, got: " + result;
    }

    @Test
    public void testColorMiniMessage() {
        // MiniMessage <color> format
        String greenResult = Colors.color("<green>Green Text");
        assert greenResult.startsWith("§a") : "Expected green (§a), got: " + greenResult;

        String boldResult = Colors.color("<bold>Bold");
        assert boldResult.startsWith("§l") : "Expected bold (§l), got: " + boldResult;
    }

    @Test
    public void testColorMiniMessageHex() {
        // MiniMessage hex <#FFFFFF> format - use non-standard color to avoid legacy fallback
        String result = Colors.color("<#12AB9F>Teal").toLowerCase();
        assert result.startsWith("§x§1§2§a§b§9§f") : "Expected hex color, got: " + result;
    }

    @Test
    public void testColorMixed() {
        // Mixed formats
        String result = Colors.color("<aqua>Mini &lLegacy &#333333Hex").toLowerCase();
        assert result.contains("§b") : "Expected aqua (§b) in: " + result;
        assert result.contains("§l") : "Expected bold (§l) in: " + result;
        assert result.contains("§x§3§3§3§3§3§3") : "Expected hex in: " + result;
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
