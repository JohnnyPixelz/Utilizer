package io.github.johnnypixelz.utilizer.text;

import org.junit.Test;

import static org.junit.Assert.*;

public class ColorsTest {

    @Test
    public void testMiniMessageWithLegacyCodes() {
        // This tests the case where a placeholder contains legacy § color codes
        // inside a MiniMessage formatted string (e.g., <gradient>)
        // This should not throw an exception
        String input = "<gradient:#2ECC71:#52D68A>You collected §asome item</gradient>";
        String result = Colors.color(input);
        assertNotNull(result);
    }

    @Test
    public void testMiniMessageWithLegacyHexCodes() {
        // This tests the case from the bug report where hex colors using §x format
        // are inside MiniMessage tags
        String input = "<gradient:#2ECC71:#52D68A>You collected §x§f§f§6§9§8§4Name</gradient>";
        String result = Colors.color(input);
        assertNotNull(result);
    }

    @Test
    public void testPureMiniMessage() {
        String input = "<green>Hello <bold>World</bold></green>";
        String result = Colors.color(input);
        assertNotNull(result);
        assertTrue(result.contains("§a")); // green = §a
    }

    @Test
    public void testPureLegacyAmpersand() {
        String input = "&aHello &lWorld";
        String result = Colors.color(input);
        assertNotNull(result);
        assertTrue(result.contains("§a"));
        assertTrue(result.contains("§l"));
    }

    @Test
    public void testHexColorCodes() {
        String input = "&#FFFFFFWhite text";
        String result = Colors.color(input);
        assertNotNull(result);
    }

}
