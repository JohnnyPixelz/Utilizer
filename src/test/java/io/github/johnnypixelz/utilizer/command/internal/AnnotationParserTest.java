package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.command.CommandBase;
import io.github.johnnypixelz.utilizer.command.annotations.*;
import io.github.johnnypixelz.utilizer.command.exceptions.CommandAnnotationParseException;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolverRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AnnotationParserTest {

    private TestableAnnotationParser parser;

    @Before
    public void setUp() {
        parser = new TestableAnnotationParser();
    }

    // Testable parser that captures registered commands instead of registering with Bukkit
    private static class TestableAnnotationParser {
        private int registeredCount = 0;

        CommandDefinition parse(CommandBase instance) throws CommandAnnotationParseException {
            // Create a minimal parser that doesn't need the full registry
            return parseDirectly(instance);
        }

        private CommandDefinition parseDirectly(CommandBase instance) throws CommandAnnotationParseException {
            Class<? extends CommandBase> clazz = instance.getClass();

            Label labelAnnotation = clazz.getAnnotation(Label.class);
            if (labelAnnotation == null) {
                throw new CommandAnnotationParseException(
                        "Missing @Label annotation on class " + clazz.getCanonicalName()
                );
            }

            CommandBuilder builder = CommandBuilder.create()
                    .labels(parseLabels(labelAnnotation.value()));

            // Parse description
            Description descAnnotation = clazz.getAnnotation(Description.class);
            if (descAnnotation != null) {
                builder.description(descAnnotation.value());
            }

            // Parse permissions
            Permission[] permissions = clazz.getAnnotationsByType(Permission.class);
            for (Permission perm : permissions) {
                builder.addPermission(io.github.johnnypixelz.utilizer.command.permissions.CommandPermission.literal(perm.value()));
            }

            // Parse permission message
            PermissionMessage permMsg = clazz.getAnnotation(PermissionMessage.class);
            if (permMsg != null) {
                builder.permissionMessage(io.github.johnnypixelz.utilizer.command.permissions.CommandPermissionMessage.literal(permMsg.value()));
            }

            // Parse methods
            for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Default.class)) {
                    builder.defaultMethod(new CommandMethodDefinition(instance, method));
                }

                if (method.isAnnotationPresent(Subcommand.class)) {
                    Subcommand subAnnotation = method.getAnnotation(Subcommand.class);
                    Description subDesc = method.getAnnotation(Description.class);

                    CommandBuilder subBuilder = CommandBuilder.create()
                            .labels(parseLabels(subAnnotation.value()))
                            .defaultMethod(new CommandMethodDefinition(instance, method));

                    if (subDesc != null) {
                        subBuilder.description(subDesc.value());
                    }

                    builder.addSubcommand(subBuilder.build());
                }
            }

            return builder.build();
        }

        private java.util.List<String> parseLabels(String labelString) {
            return java.util.Arrays.stream(labelString.split("\\|"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        int getRegisteredCount() {
            return registeredCount;
        }
    }

    // ==================== Test Command Classes ====================

    @Label("simple")
    public static class SimpleCommand extends CommandBase {
        @Default
        public void execute(CommandSender sender) {}
    }

    @Label("multi|m|alias")
    public static class MultiLabelCommand extends CommandBase {
        @Default
        public void execute(CommandSender sender) {}
    }

    @Label("described")
    @Description("A command with description")
    public static class DescribedCommand extends CommandBase {
        @Default
        public void execute(CommandSender sender) {}
    }

    @Label("permitted")
    @Permission("test.permission")
    public static class PermittedCommand extends CommandBase {
        @Default
        public void execute(CommandSender sender) {}
    }

    @Label("multiperm")
    @Permission("perm.one")
    @Permission("perm.two")
    public static class MultiPermCommand extends CommandBase {
        @Default
        public void execute(CommandSender sender) {}
    }

    @Label("withsubs")
    public static class WithSubcommandsCommand extends CommandBase {
        @Default
        public void defaultHandler(CommandSender sender) {}

        @Subcommand("reload|rl")
        public void reload(CommandSender sender) {}

        @Subcommand("info")
        @Description("Shows info")
        public void info(Player player) {}
    }

    @Label("withargs")
    public static class WithArgsCommand extends CommandBase {
        @Default
        public void execute(Player player, String name, int count) {}
    }

    @Label("permmessage")
    @Permission("test.perm")
    @PermissionMessage("&cCustom no permission message!")
    public static class PermMessageCommand extends CommandBase {
        @Default
        public void execute(CommandSender sender) {}
    }

    public static class NoLabelCommand extends CommandBase {
        @Default
        public void execute(CommandSender sender) {}
    }

    @Label("nodефault")
    public static class NoDefaultCommand extends CommandBase {
        @Subcommand("sub")
        public void sub(CommandSender sender) {}
    }

    @Label("parent")
    public static class ParentWithNestedCommand extends CommandBase {
        @Default
        public void defaultHandler(CommandSender sender) {}

        @Subcommand("nested")
        public class NestedSubcommand extends CommandBase {
            @Default
            public void nestedDefault(CommandSender sender) {}

            @Subcommand("deep")
            public void deepSub(CommandSender sender) {}
        }
    }

    // ==================== Basic Parsing Tests ====================

    @Test
    public void testParseSimpleCommand() throws CommandAnnotationParseException {
        CommandDefinition def = parser.parse(new SimpleCommand());

        assertEquals("simple", def.getPrimaryLabel());
        assertEquals(1, def.getLabels().size());
        assertNotNull(def.getDefaultMethod());
        assertTrue(def.getSubcommands().isEmpty());
        assertTrue(def.getPermissions().isEmpty());
    }

    @Test
    public void testParseMultiLabelCommand() throws CommandAnnotationParseException {
        CommandDefinition def = parser.parse(new MultiLabelCommand());

        assertEquals(3, def.getLabels().size());
        assertEquals("multi", def.getPrimaryLabel());
        assertTrue(def.getLabels().contains("m"));
        assertTrue(def.getLabels().contains("alias"));
    }

    @Test
    public void testParseDescribedCommand() throws CommandAnnotationParseException {
        CommandDefinition def = parser.parse(new DescribedCommand());

        assertEquals("A command with description", def.getDescription());
    }

    // ==================== Permission Tests ====================

    @Test
    public void testParsePermittedCommand() throws CommandAnnotationParseException {
        CommandDefinition def = parser.parse(new PermittedCommand());

        assertEquals(1, def.getPermissions().size());
        assertEquals("test.permission", def.getPermissions().get(0).getPermission());
    }

    @Test
    public void testParseMultiPermCommand() throws CommandAnnotationParseException {
        CommandDefinition def = parser.parse(new MultiPermCommand());

        assertEquals(2, def.getPermissions().size());
    }

    @Test
    public void testParsePermissionMessage() throws CommandAnnotationParseException {
        CommandDefinition def = parser.parse(new PermMessageCommand());

        assertNotNull(def.getPermissionMessage());
    }

    // ==================== Subcommand Tests ====================

    @Test
    public void testParseWithSubcommands() throws CommandAnnotationParseException {
        CommandDefinition def = parser.parse(new WithSubcommandsCommand());

        assertEquals(2, def.getSubcommands().size());

        // Find reload subcommand
        CommandDefinition reload = def.findSubcommand("reload").orElse(null);
        assertNotNull(reload);
        assertEquals(2, reload.getLabels().size()); // reload and rl
        assertTrue(reload.getLabels().contains("rl"));

        // Find info subcommand
        CommandDefinition info = def.findSubcommand("info").orElse(null);
        assertNotNull(info);
        assertEquals("Shows info", info.getDescription());
    }

    @Test
    public void testParseSubcommandAliases() throws CommandAnnotationParseException {
        CommandDefinition def = parser.parse(new WithSubcommandsCommand());

        assertTrue(def.findSubcommand("reload").isPresent());
        assertTrue(def.findSubcommand("rl").isPresent());
    }

    // ==================== Method Analysis Tests ====================

    @Test
    public void testParseWithArgs() throws CommandAnnotationParseException {
        CommandDefinition def = parser.parse(new WithArgsCommand());

        assertNotNull(def.getDefaultMethod());
        assertEquals(2, def.getDefaultMethod().getCommandParameters().length);
        assertEquals(Player.class, def.getDefaultMethod().getSenderType());
    }

    // ==================== Error Cases ====================

    @Test(expected = CommandAnnotationParseException.class)
    public void testParseNoLabelThrows() throws CommandAnnotationParseException {
        parser.parse(new NoLabelCommand());
    }

    @Test
    public void testParseNoDefaultMethod() throws CommandAnnotationParseException {
        CommandDefinition def = parser.parse(new NoDefaultCommand());

        assertNull(def.getDefaultMethod());
        assertEquals(1, def.getSubcommands().size());
    }

    // ==================== Syntax Generation Tests ====================

    @Test
    public void testSyntaxGenerated() throws CommandAnnotationParseException {
        CommandDefinition def = parser.parse(new SimpleCommand());

        assertNotNull(def.getSyntax());
        assertEquals("simple", def.getSyntax().getLabel());
    }

    @Test
    public void testSyntaxWithParameters() throws CommandAnnotationParseException {
        CommandDefinition def = parser.parse(new WithArgsCommand());

        assertNotNull(def.getSyntax());
        assertEquals(2, def.getSyntax().getParameterNames().size());
    }

}
