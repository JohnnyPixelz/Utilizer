package io.github.johnnypixelz.utilizer.command.internal;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class SyntaxGeneratorTest {

    // Test methods for creating CommandMethodDefinition
    public static class TestMethods {
        public void noArgs(CommandSender sender) {}
        public void oneArg(Player player, String name) {}
        public void multiArgs(CommandSender sender, String name, int count, boolean flag) {}
        public void noSender(String text) {}
    }

    private CommandMethodDefinition createMethod(String methodName, Class<?>... paramTypes) throws Exception {
        Method method = TestMethods.class.getMethod(methodName, paramTypes);
        return new CommandMethodDefinition(new TestMethods(), method);
    }

    // ==================== Basic Syntax Tests ====================

    @Test
    public void testSimpleCommandSyntax() throws Exception {
        CommandDefinition def = CommandBuilder.create()
                .labels("test")
                .defaultMethod(createMethod("noArgs", CommandSender.class))
                .build();

        SyntaxGenerator syntax = def.getSyntax();

        assertEquals("test", syntax.getLabel());
        assertTrue(syntax.getSubLabels().isEmpty());
        assertTrue(syntax.getParameterNames().isEmpty());
    }

    @Test
    public void testCommandWithOneParameter() throws Exception {
        CommandDefinition def = CommandBuilder.create()
                .labels("greet")
                .defaultMethod(createMethod("oneArg", Player.class, String.class))
                .build();

        SyntaxGenerator syntax = def.getSyntax();

        assertEquals("greet", syntax.getLabel());
        assertEquals(1, syntax.getParameterNames().size());
        // Parameter name depends on compilation with -parameters flag
    }

    @Test
    public void testCommandWithMultipleParameters() throws Exception {
        CommandDefinition def = CommandBuilder.create()
                .labels("cmd")
                .defaultMethod(createMethod("multiArgs", CommandSender.class, String.class, int.class, boolean.class))
                .build();

        SyntaxGenerator syntax = def.getSyntax();

        assertEquals(3, syntax.getParameterNames().size());
    }

    // ==================== Subcommand Syntax Tests ====================

    @Test
    public void testSubcommandSyntax() throws Exception {
        CommandDefinition parent = CommandBuilder.create()
                .labels("admin")
                .build();

        CommandDefinition child = CommandBuilder.create()
                .labels("reload")
                .parent(parent)
                .defaultMethod(createMethod("noArgs", CommandSender.class))
                .build();

        SyntaxGenerator syntax = child.getSyntax();

        assertEquals("admin", syntax.getLabel());
        assertEquals(1, syntax.getSubLabels().size());
        assertEquals("reload", syntax.getSubLabels().get(0));
    }

    @Test
    public void testNestedSubcommandSyntax() throws Exception {
        CommandDefinition root = CommandBuilder.create()
                .labels("plugin")
                .build();

        CommandDefinition level1 = CommandBuilder.create()
                .labels("config")
                .parent(root)
                .build();

        CommandDefinition level2 = CommandBuilder.create()
                .labels("reset")
                .parent(level1)
                .defaultMethod(createMethod("noArgs", CommandSender.class))
                .build();

        SyntaxGenerator syntax = level2.getSyntax();

        assertEquals("plugin", syntax.getLabel());
        assertEquals(2, syntax.getSubLabels().size());
        assertEquals("config", syntax.getSubLabels().get(0));
        assertEquals("reset", syntax.getSubLabels().get(1));
    }

    // ==================== Generate Output Tests ====================

    @Test
    public void testGenerateSimple() throws Exception {
        CommandDefinition def = CommandBuilder.create()
                .labels("help")
                .defaultMethod(createMethod("noArgs", CommandSender.class))
                .build();

        String syntax = def.getSyntax().generate();

        assertTrue(syntax.contains("/help"));
    }

    @Test
    public void testGenerateWithSubcommand() throws Exception {
        CommandDefinition parent = CommandBuilder.create()
                .labels("admin")
                .build();

        CommandDefinition child = CommandBuilder.create()
                .labels("reload")
                .parent(parent)
                .defaultMethod(createMethod("noArgs", CommandSender.class))
                .build();

        String syntax = child.getSyntax().generate();

        assertTrue(syntax.contains("/admin"));
        assertTrue(syntax.contains("reload"));
    }

    // ==================== Immutability Tests ====================

    @Test
    public void testSubLabelsImmutable() throws Exception {
        CommandDefinition parent = CommandBuilder.create().labels("parent").build();
        CommandDefinition child = CommandBuilder.create()
                .labels("child")
                .parent(parent)
                .defaultMethod(createMethod("noArgs", CommandSender.class))
                .build();

        try {
            child.getSyntax().getSubLabels().add("new");
            fail("Should not be able to modify subLabels");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void testParameterNamesImmutable() throws Exception {
        CommandDefinition def = CommandBuilder.create()
                .labels("test")
                .defaultMethod(createMethod("oneArg", Player.class, String.class))
                .build();

        try {
            def.getSyntax().getParameterNames().add("new");
            fail("Should not be able to modify parameterNames");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

}
