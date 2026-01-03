package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.internal.CommandDefinition;
import org.bukkit.entity.Player;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for programmatic command creation via fluent builder.
 */
public class ProgrammaticCommandTest {

    // ==================== Basic Builder Tests ====================

    @Test
    public void testCreate_singleLabel_setsLabel() {
        ProgrammaticCommand cmd = ProgrammaticCommand.create("test");

        assertEquals("test", cmd.getPrimaryLabel());
        assertEquals(1, cmd.getLabels().size());
    }

    @Test
    public void testCreate_withAliases_setsAllLabels() {
        ProgrammaticCommand cmd = ProgrammaticCommand.create("test", "t", "tst");

        assertEquals("test", cmd.getPrimaryLabel());
        assertEquals(3, cmd.getLabels().size());
        assertTrue(cmd.getLabels().contains("t"));
        assertTrue(cmd.getLabels().contains("tst"));
    }

    @Test
    public void testDescription_setsDescription() {
        CommandDefinition def = ProgrammaticCommand.create("test")
                .description("A test command")
                .build();

        assertEquals("A test command", def.getDescription());
    }

    @Test
    public void testPermission_addsPermission() {
        CommandDefinition def = ProgrammaticCommand.create("test")
                .permission("test.use")
                .build();

        assertEquals(1, def.getPermissions().size());
    }

    @Test
    public void testMultiplePermissions_addsAll() {
        CommandDefinition def = ProgrammaticCommand.create("test")
                .permission("test.use")
                .permission("test.admin")
                .build();

        assertEquals(2, def.getPermissions().size());
    }

    // ==================== Handler Tests ====================

    @Test
    public void testExecutes_setsDefaultMethod() {
        List<String> capturedArgs = new ArrayList<>();

        CommandDefinition def = ProgrammaticCommand.create("test")
                .executes((sender, args) -> capturedArgs.addAll(args))
                .build();

        assertNotNull(def.getDefaultMethod());
    }

    @Test
    public void testExecutes_withSenderType_setsSenderType() {
        CommandDefinition def = ProgrammaticCommand.create("test")
                .executes(Player.class, (player, args) -> {
                    // Player-specific logic
                })
                .build();

        assertNotNull(def.getDefaultMethod());
        assertEquals(Player.class, def.getDefaultMethod().getSenderType());
    }

    // ==================== Subcommand Tests ====================

    @Test
    public void testSubcommand_addsSubcommand() {
        CommandDefinition def = ProgrammaticCommand.create("parent")
                .subcommand("child", sub -> sub
                        .description("A child command")
                )
                .build();

        assertEquals(1, def.getSubcommands().size());
        assertEquals("child", def.getSubcommands().get(0).getPrimaryLabel());
    }

    @Test
    public void testSubcommand_withAliases_setsAliases() {
        CommandDefinition def = ProgrammaticCommand.create("parent")
                .subcommand("child", new String[]{"c", "ch"}, sub -> sub
                        .description("A child command")
                )
                .build();

        assertEquals(1, def.getSubcommands().size());
        assertEquals(3, def.getSubcommands().get(0).getLabels().size());
    }

    @Test
    public void testNestedSubcommands_buildsCorrectly() {
        CommandDefinition def = ProgrammaticCommand.create("grandparent")
                .subcommand("parent", parent -> parent
                        .subcommand("child", child -> child
                                .description("Deepest level")
                        )
                )
                .build();

        assertEquals(1, def.getSubcommands().size());
        CommandDefinition parentDef = def.getSubcommands().get(0);
        assertEquals(1, parentDef.getSubcommands().size());
        assertEquals("child", parentDef.getSubcommands().get(0).getPrimaryLabel());
    }

    @Test
    public void testSubcommand_inheritsParentInSyntax() {
        CommandDefinition def = ProgrammaticCommand.create("parent")
                .subcommand("child", sub -> sub
                        .executes((sender, args) -> {})
                )
                .build();

        CommandDefinition childDef = def.getSubcommands().get(0);
        // The child's syntax should include the parent
        String syntax = childDef.getSyntax().generate();
        assertTrue(syntax.contains("parent"));
        assertTrue(syntax.contains("child"));
    }

    // ==================== Complex Command Tests ====================

    @Test
    public void testComplexCommand_buildsCorrectly() {
        CommandDefinition def = ProgrammaticCommand.create("economy", "eco", "money")
                .description("Economy commands")
                .permission("economy.use")
                .executes((sender, args) -> {
                    // Default handler shows balance
                })
                .subcommand("give", sub -> sub
                        .description("Give money to a player")
                        .permission("economy.give")
                        .executes((sender, args) -> {
                            // Give logic
                        })
                )
                .subcommand("take", sub -> sub
                        .description("Take money from a player")
                        .permission("economy.take")
                        .executes((sender, args) -> {
                            // Take logic
                        })
                )
                .subcommand("set", sub -> sub
                        .description("Set a player's balance")
                        .permission("economy.set")
                )
                .build();

        // Verify structure
        assertEquals("economy", def.getPrimaryLabel());
        assertEquals(3, def.getLabels().size());
        assertEquals("Economy commands", def.getDescription());
        assertEquals(1, def.getPermissions().size());
        assertNotNull(def.getDefaultMethod());
        assertEquals(3, def.getSubcommands().size());

        // Verify subcommands
        CommandDefinition giveDef = def.findSubcommand("give").orElse(null);
        assertNotNull(giveDef);
        assertEquals("Give money to a player", giveDef.getDescription());
    }

    // ==================== Build Without Register Tests ====================

    @Test
    public void testBuild_doesNotRegister() {
        // Just build, don't register - this should not throw
        CommandDefinition def = ProgrammaticCommand.create("unregistered")
                .description("This command is not registered")
                .build();

        assertNotNull(def);
        assertEquals("unregistered", def.getPrimaryLabel());
    }

    // ==================== Permission Message Tests ====================

    @Test
    public void testPermissionMessage_setsMessage() {
        CommandDefinition def = ProgrammaticCommand.create("test")
                .permission("test.use")
                .permissionMessage("You don't have permission!")
                .build();

        assertNotNull(def.getPermissionMessage());
    }

    // ==================== Edge Cases ====================

    @Test
    public void testNoHandler_noDefaultMethod() {
        CommandDefinition def = ProgrammaticCommand.create("empty")
                .description("No handler")
                .build();

        assertNull(def.getDefaultMethod());
    }

    @Test
    public void testNoDescription_nullDescription() {
        CommandDefinition def = ProgrammaticCommand.create("nodesc").build();

        assertNull(def.getDescription());
    }

    @Test
    public void testNoPermissions_emptyList() {
        CommandDefinition def = ProgrammaticCommand.create("noperm").build();

        assertTrue(def.getPermissions().isEmpty());
    }

    @Test
    public void testNoSubcommands_emptyList() {
        CommandDefinition def = ProgrammaticCommand.create("nosub").build();

        assertTrue(def.getSubcommands().isEmpty());
    }

}
