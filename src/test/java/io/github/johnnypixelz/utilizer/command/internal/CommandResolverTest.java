package io.github.johnnypixelz.utilizer.command.internal;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class CommandResolverTest {

    private CommandDefinition rootCommand;
    private CommandDefinition reloadSubcommand;
    private CommandDefinition configSubcommand;
    private CommandDefinition configResetSubcommand;

    @Before
    public void setUp() {
        // Build a command tree:
        // /admin
        //   /admin reload
        //   /admin config
        //     /admin config reset

        configResetSubcommand = CommandBuilder.create()
                .labels("reset", "r")
                .description("Reset config")
                .build();

        configSubcommand = CommandBuilder.create()
                .labels("config", "cfg")
                .description("Config management")
                .addSubcommand(configResetSubcommand)
                .build();

        reloadSubcommand = CommandBuilder.create()
                .labels("reload", "rl")
                .description("Reload plugin")
                .build();

        rootCommand = CommandBuilder.create()
                .labels("admin", "adm")
                .description("Admin commands")
                .addSubcommand(reloadSubcommand)
                .addSubcommand(configSubcommand)
                .build();
    }

    // ==================== Basic Resolution Tests ====================

    @Test
    public void testResolveNoArgs() {
        CommandResolver.Result result = CommandResolver.resolve(rootCommand, Collections.emptyList());

        assertEquals(rootCommand, result.command());
        assertTrue(result.remainingArgs().isEmpty());
    }

    @Test
    public void testResolveToSubcommand() {
        List<String> args = Arrays.asList("reload");
        CommandResolver.Result result = CommandResolver.resolve(rootCommand, args);

        assertEquals(reloadSubcommand, result.command());
        assertTrue(result.remainingArgs().isEmpty());
    }

    @Test
    public void testResolveToSubcommandWithAlias() {
        List<String> args = Arrays.asList("rl");
        CommandResolver.Result result = CommandResolver.resolve(rootCommand, args);

        assertEquals(reloadSubcommand, result.command());
        assertTrue(result.remainingArgs().isEmpty());
    }

    @Test
    public void testResolveToNestedSubcommand() {
        List<String> args = Arrays.asList("config", "reset");
        CommandResolver.Result result = CommandResolver.resolve(rootCommand, args);

        assertEquals(configResetSubcommand, result.command());
        assertTrue(result.remainingArgs().isEmpty());
    }

    @Test
    public void testResolveToNestedSubcommandWithAliases() {
        List<String> args = Arrays.asList("cfg", "r");
        CommandResolver.Result result = CommandResolver.resolve(rootCommand, args);

        assertEquals(configResetSubcommand, result.command());
        assertTrue(result.remainingArgs().isEmpty());
    }

    // ==================== Remaining Args Tests ====================

    @Test
    public void testResolveWithRemainingArgs() {
        List<String> args = Arrays.asList("reload", "arg1", "arg2");
        CommandResolver.Result result = CommandResolver.resolve(rootCommand, args);

        assertEquals(reloadSubcommand, result.command());
        assertEquals(2, result.remainingArgs().size());
        assertEquals("arg1", result.remainingArgs().get(0));
        assertEquals("arg2", result.remainingArgs().get(1));
    }

    @Test
    public void testResolveUnknownSubcommandReturnsParent() {
        List<String> args = Arrays.asList("unknown", "arg1");
        CommandResolver.Result result = CommandResolver.resolve(rootCommand, args);

        assertEquals(rootCommand, result.command());
        assertEquals(2, result.remainingArgs().size());
        assertEquals("unknown", result.remainingArgs().get(0));
    }

    @Test
    public void testResolvePartialMatch() {
        // /admin config unknown -> should stop at config
        List<String> args = Arrays.asList("config", "unknown", "arg");
        CommandResolver.Result result = CommandResolver.resolve(rootCommand, args);

        assertEquals(configSubcommand, result.command());
        assertEquals(2, result.remainingArgs().size());
        assertEquals("unknown", result.remainingArgs().get(0));
    }

    // ==================== Case Insensitivity Tests ====================

    @Test
    public void testResolveCaseInsensitive() {
        List<String> args = Arrays.asList("RELOAD");
        CommandResolver.Result result = CommandResolver.resolve(rootCommand, args);

        assertEquals(reloadSubcommand, result.command());
    }

    @Test
    public void testResolveMixedCase() {
        List<String> args = Arrays.asList("CoNfIg", "ReSeT");
        CommandResolver.Result result = CommandResolver.resolve(rootCommand, args);

        assertEquals(configResetSubcommand, result.command());
    }

    // ==================== Edge Cases ====================

    @Test
    public void testResolveEmptyString() {
        List<String> args = Arrays.asList("");
        CommandResolver.Result result = CommandResolver.resolve(rootCommand, args);

        // Empty string doesn't match any subcommand
        assertEquals(rootCommand, result.command());
        assertEquals(1, result.remainingArgs().size());
    }

    @Test
    public void testResolveDeepNesting() {
        // Create a deeply nested structure
        CommandDefinition level3 = CommandBuilder.create().labels("level3").build();
        CommandDefinition level2 = CommandBuilder.create().labels("level2").addSubcommand(level3).build();
        CommandDefinition level1 = CommandBuilder.create().labels("level1").addSubcommand(level2).build();
        CommandDefinition root = CommandBuilder.create().labels("root").addSubcommand(level1).build();

        List<String> args = Arrays.asList("level1", "level2", "level3", "remaining");
        CommandResolver.Result result = CommandResolver.resolve(root, args);

        assertEquals(level3, result.command());
        assertEquals(1, result.remainingArgs().size());
        assertEquals("remaining", result.remainingArgs().get(0));
    }

    @Test
    public void testResolveNoSubcommands() {
        CommandDefinition simple = CommandBuilder.create()
                .labels("simple")
                .build();

        List<String> args = Arrays.asList("arg1", "arg2");
        CommandResolver.Result result = CommandResolver.resolve(simple, args);

        assertEquals(simple, result.command());
        assertEquals(2, result.remainingArgs().size());
    }

}
