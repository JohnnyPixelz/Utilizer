package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.command.permissions.CommandPermission;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermissionMessage;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CommandBuilderTest {

    // ==================== Basic Building Tests ====================

    @Test
    public void testBuildSimpleCommand() {
        CommandDefinition def = CommandBuilder.create()
                .labels("test")
                .build();

        assertEquals(1, def.getLabels().size());
        assertEquals("test", def.getPrimaryLabel());
    }

    @Test
    public void testBuildWithMultipleLabels() {
        CommandDefinition def = CommandBuilder.create()
                .labels("teleport", "tp", "goto")
                .build();

        assertEquals(3, def.getLabels().size());
        assertEquals("teleport", def.getPrimaryLabel());
        assertTrue(def.getLabels().contains("tp"));
        assertTrue(def.getLabels().contains("goto"));
    }

    @Test
    public void testBuildWithLabelsList() {
        CommandDefinition def = CommandBuilder.create()
                .labels(List.of("help", "?", "h"))
                .build();

        assertEquals(3, def.getLabels().size());
        assertEquals("help", def.getPrimaryLabel());
    }

    @Test
    public void testAddLabel() {
        CommandDefinition def = CommandBuilder.create()
                .addLabel("cmd")
                .addLabel("c")
                .build();

        assertEquals(2, def.getLabels().size());
        assertEquals("cmd", def.getPrimaryLabel());
    }

    // ==================== Description Tests ====================

    @Test
    public void testBuildWithDescription() {
        CommandDefinition def = CommandBuilder.create()
                .labels("test")
                .description("A test command")
                .build();

        assertEquals("A test command", def.getDescription());
    }

    @Test
    public void testBuildWithoutDescription() {
        CommandDefinition def = CommandBuilder.create()
                .labels("test")
                .build();

        assertNull(def.getDescription());
    }

    // ==================== Permission Tests ====================

    @Test
    public void testBuildWithPermission() {
        CommandPermission perm = CommandPermission.literal("test.permission");

        CommandDefinition def = CommandBuilder.create()
                .labels("test")
                .addPermission(perm)
                .build();

        assertEquals(1, def.getPermissions().size());
        assertEquals("test.permission", def.getPermissions().get(0).getPermission());
    }

    @Test
    public void testBuildWithMultiplePermissions() {
        CommandDefinition def = CommandBuilder.create()
                .labels("test")
                .addPermission(CommandPermission.literal("perm.one"))
                .addPermission(CommandPermission.literal("perm.two"))
                .addPermission(CommandPermission.literal("perm.three"))
                .build();

        assertEquals(3, def.getPermissions().size());
    }

    @Test
    public void testBuildWithPermissionMessage() {
        CommandPermissionMessage msg = CommandPermissionMessage.literal("No access!");

        CommandDefinition def = CommandBuilder.create()
                .labels("test")
                .permissionMessage(msg)
                .build();

        assertNotNull(def.getPermissionMessage());
    }

    // ==================== Subcommand Tests ====================

    @Test
    public void testBuildWithSubcommand() {
        CommandDefinition subcommand = CommandBuilder.create()
                .labels("sub")
                .description("A subcommand")
                .build();

        CommandDefinition def = CommandBuilder.create()
                .labels("parent")
                .addSubcommand(subcommand)
                .build();

        assertEquals(1, def.getSubcommands().size());
        assertEquals("sub", def.getSubcommands().get(0).getPrimaryLabel());
    }

    @Test
    public void testBuildWithMultipleSubcommands() {
        CommandDefinition sub1 = CommandBuilder.create().labels("sub1").build();
        CommandDefinition sub2 = CommandBuilder.create().labels("sub2").build();
        CommandDefinition sub3 = CommandBuilder.create().labels("sub3").build();

        CommandDefinition def = CommandBuilder.create()
                .labels("parent")
                .addSubcommand(sub1)
                .addSubcommand(sub2)
                .addSubcommand(sub3)
                .build();

        assertEquals(3, def.getSubcommands().size());
    }

    // ==================== Parent Tests ====================

    @Test
    public void testBuildWithParent() {
        CommandDefinition parent = CommandBuilder.create()
                .labels("parent")
                .build();

        CommandDefinition child = CommandBuilder.create()
                .labels("child")
                .parent(parent)
                .build();

        assertNotNull(child.getParent());
        assertEquals("parent", child.getParent().getPrimaryLabel());
    }

    @Test
    public void testBuildWithoutParent() {
        CommandDefinition def = CommandBuilder.create()
                .labels("root")
                .build();

        assertNull(def.getParent());
    }

    // ==================== Immutability Tests ====================

    @Test
    public void testLabelsAreImmutable() {
        CommandDefinition def = CommandBuilder.create()
                .labels("test", "t")
                .build();

        try {
            def.getLabels().add("new");
            fail("Should not be able to modify labels");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void testSubcommandsAreImmutable() {
        CommandDefinition def = CommandBuilder.create()
                .labels("test")
                .build();

        try {
            def.getSubcommands().add(CommandBuilder.create().labels("new").build());
            fail("Should not be able to modify subcommands");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void testPermissionsAreImmutable() {
        CommandDefinition def = CommandBuilder.create()
                .labels("test")
                .build();

        try {
            def.getPermissions().add(CommandPermission.literal("new.perm"));
            fail("Should not be able to modify permissions");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    // ==================== findSubcommand Tests ====================

    @Test
    public void testFindSubcommand() {
        CommandDefinition sub = CommandBuilder.create().labels("reload", "rl").build();

        CommandDefinition def = CommandBuilder.create()
                .labels("admin")
                .addSubcommand(sub)
                .build();

        assertTrue(def.findSubcommand("reload").isPresent());
        assertTrue(def.findSubcommand("rl").isPresent());
        assertTrue(def.findSubcommand("RELOAD").isPresent()); // Case insensitive
        assertFalse(def.findSubcommand("unknown").isPresent());
    }

    // ==================== Complex Builder Chain Tests ====================

    @Test
    public void testComplexBuilderChain() {
        CommandDefinition reload = CommandBuilder.create()
                .labels("reload", "rl")
                .description("Reload the plugin")
                .addPermission(CommandPermission.literal("admin.reload"))
                .build();

        CommandDefinition debug = CommandBuilder.create()
                .labels("debug")
                .description("Toggle debug mode")
                .build();

        CommandDefinition def = CommandBuilder.create()
                .labels("myplugin", "mp")
                .description("Main plugin command")
                .addPermission(CommandPermission.literal("myplugin.use"))
                .permissionMessage(CommandPermissionMessage.literal("&cNo permission!"))
                .addSubcommand(reload)
                .addSubcommand(debug)
                .build();

        assertEquals("myplugin", def.getPrimaryLabel());
        assertEquals(2, def.getLabels().size());
        assertEquals("Main plugin command", def.getDescription());
        assertEquals(1, def.getPermissions().size());
        assertEquals(2, def.getSubcommands().size());
        assertNotNull(def.getPermissionMessage());
    }

}
