# Command Library

An annotation-based command framework for Bukkit/Spigot plugins. Define commands declaratively using annotations - no boilerplate code required.

## Quick Start

### 1. Create a Command

```java
@Label("greet|hello")
@Description("Greets a player")
@Permission("myplugin.greet")
public class GreetCommand extends CommandBase {

    @Default
    public void execute(Player player, String name) {
        player.sendMessage("Hello, " + name + "!");
    }
}
```

### 2. Register It

```java
// In your plugin's onEnable()
Commands.register(new GreetCommand());
```

That's it! Players can now use `/greet <name>` or `/hello <name>`.

---

## Annotations

### @Label (Required)

Defines the command name and aliases. Use `|` to separate multiple names.

```java
@Label("teleport|tp|goto")  // /teleport, /tp, or /goto
public class TeleportCommand extends CommandBase { }
```

### @Subcommand

Creates a subcommand. Can be used on methods or nested classes.

```java
@Label("admin")
public class AdminCommand extends CommandBase {

    @Subcommand("reload|rl")  // /admin reload or /admin rl
    public void reload(CommandSender sender) {
        sender.sendMessage("Reloaded!");
    }

    @Subcommand("debug")  // /admin debug
    public void debug(Player player, boolean enabled) {
        player.sendMessage("Debug mode: " + enabled);
    }
}
```

### @Default

Marks the method that runs when no subcommand matches.

```java
@Label("info")
public class InfoCommand extends CommandBase {

    @Default  // Runs for /info
    public void showInfo(Player player) {
        player.sendMessage("Plugin v1.0");
    }

    @Subcommand("version")  // Runs for /info version
    public void showVersion(Player player) {
        player.sendMessage("Version: 1.0.0");
    }
}
```

### @Description

Adds a description for help text and tab completion.

```java
@Label("heal")
@Description("Heals a player to full health")
public class HealCommand extends CommandBase { }
```

### @Permission

Requires a permission to execute. Can be stacked for multiple requirements.

```java
@Label("ban")
@Permission("myplugin.ban")
@Permission("myplugin.moderate")  // Requires BOTH permissions
public class BanCommand extends CommandBase { }
```

You can also add a custom denial message:

```java
@Permission(value = "admin.use", message = "&cAdmins only!")
```

Or load the message from config:

```java
@Permission(value = "admin.use", messageConfig = "messages", messagePath = "no-permission")
```

### @PermissionMessage

Sets a custom message when any permission check fails.

```java
@Label("secret")
@Permission("vip.access")
@PermissionMessage("&cYou need VIP to use this!")
public class SecretCommand extends CommandBase { }
```

### @ConfigPermission

Loads the permission node from a config file.

```java
@Label("custom")
@ConfigPermission(config = "config", path = "permissions.custom-command")
public class CustomCommand extends CommandBase { }
```

### @PermissionConfigMessage

Loads the permission denial message from config.

```java
@Label("premium")
@Permission("premium.access")
@PermissionConfigMessage(config = "messages", path = "premium-required")
public class PremiumCommand extends CommandBase { }
```

---

## Method Parameters

### Sender Parameter

The first parameter can be a `CommandSender` or any subtype:

```java
@Default
public void execute(CommandSender sender) { }  // Anyone

@Default
public void execute(Player player) { }  // Players only

@Default
public void execute(ConsoleCommandSender console) { }  // Console only
```

### Argument Parameters

Subsequent parameters are resolved from command arguments:

```java
@Default
public void execute(Player player, String name, int amount, boolean confirm) {
    // /command <name> <amount> <confirm>
}
```

**Supported types:**
- `String` - Raw text (last String parameter captures remaining args)
- `int`, `Integer`, `long`, `Long`, `double`, `Double`, `float`, `Float`, `short`, `Short`
- `boolean`, `Boolean` - Accepts: true/false, yes/no, on/off, 1/0
- `char`, `Character` - Single character
- `Player` - Online player by name
- `OfflinePlayer` - Any player by name
- `Enum` - Any enum type (case-insensitive)
- `String[]` - Space-separated array

### Custom Type Resolvers

Register your own type resolvers:

```java
Commands.registerResolver(UUID.class, context -> {
    try {
        return UUID.fromString(context.getArgument());
    } catch (IllegalArgumentException e) {
        throw new ArgumentResolutionException("Invalid UUID: " + context.getArgument());
    }
});

Commands.registerResolver(Material.class, context -> {
    Material mat = Material.matchMaterial(context.getArgument());
    if (mat == null) {
        throw new ArgumentResolutionException("Unknown material: " + context.getArgument());
    }
    return mat;
});
```

---

## Nested Subcommands

Use inner classes for complex command hierarchies:

```java
@Label("guild")
public class GuildCommand extends CommandBase {

    @Default
    public void help(Player player) {
        player.sendMessage("/guild <create|invite|leave>");
    }

    @Subcommand("create")
    public class CreateSubcommand extends CommandBase {

        @Default
        public void create(Player player, String name) {
            player.sendMessage("Created guild: " + name);
        }
    }

    @Subcommand("invite")
    public class InviteSubcommand extends CommandBase {

        @Default
        public void invite(Player player, Player target) {
            target.sendMessage(player.getName() + " invited you!");
        }

        @Subcommand("cancel")  // /guild invite cancel <player>
        public void cancel(Player player, Player target) {
            target.sendMessage("Invitation cancelled.");
        }
    }
}
```

---

## Dependency Injection

Since commands are registered as instances, you can inject dependencies:

```java
public class EconomyCommand extends CommandBase {
    private final Economy economy;
    private final ConfigManager config;

    public EconomyCommand(Economy economy, ConfigManager config) {
        this.economy = economy;
        this.config = config;
    }

    @Default
    public void balance(Player player) {
        double bal = economy.getBalance(player);
        player.sendMessage(config.getMessage("balance", bal));
    }
}

// Registration
Commands.register(new EconomyCommand(economy, configManager));
```

---

## Programmatic Commands

Create commands without annotations using the fluent builder API:

```java
Commands.create("greet", "hello")
    .description("Greets someone")
    .permission("myplugin.greet")
    .executes((sender, args) -> {
        String name = args.isEmpty() ? "World" : args.get(0);
        sender.sendMessage("Hello, " + name + "!");
    })
    .register();
```

### With Subcommands

```java
Commands.create("economy", "eco", "money")
    .description("Economy commands")
    .permission("economy.use")
    .executes((sender, args) -> {
        sender.sendMessage("Use /eco <give|take|balance>");
    })
    .subcommand("give", sub -> sub
        .description("Give money to a player")
        .permission("economy.give")
        .executes((sender, args) -> {
            // args.get(0) = player name
            // args.get(1) = amount
        })
    )
    .subcommand("balance", new String[]{"bal", "b"}, sub -> sub
        .description("Check your balance")
        .executes((sender, args) -> {
            // Show balance
        })
    )
    .register();
```

### Player-Only Commands

```java
Commands.create("fly")
    .executes(Player.class, (player, args) -> {
        player.setAllowFlight(!player.getAllowFlight());
        player.sendMessage("Flight toggled!");
    })
    .register();
```

### Build Without Registering

```java
// Build the definition for inspection/testing
CommandDefinition def = Commands.create("test")
    .description("A test command")
    .build();  // Does not register

// Later register it manually if needed
```

---

## Unregistering Commands

Commands can be unregistered at runtime with zero impact on the server:

```java
// Unregister by label
Commands.unregister("mycommand");

// Unregister by command definition
CommandDefinition def = Commands.findCommand("mycommand");
if (def != null) {
    Commands.unregister(def);
}

// Unregister all commands registered through this library
Commands.unregisterAll();
```

This is useful for:
- Plugin reloads
- Dynamic command systems
- Feature toggles
- Testing

---

## Debug Mode

Enable debug logging to troubleshoot command issues:

```java
Commands.setDebugEnabled(true);
```

This logs command registration, execution, subcommand resolution, and argument parsing.

---

## Custom Messages

Override default error messages:

```java
// Hardcoded message
CommandMessageManager.setMessage(CommandMessage.NO_PERMISSION,
    Messages.literal("&cYou don't have permission!"));

// From config
CommandMessageManager.setMessage(CommandMessage.NOT_ENOUGH_ARGUMENTS,
    "messages", "not-enough-args");
```

Available messages:
- `NO_PERMISSION` - When sender lacks permission
- `NOT_ENOUGH_ARGUMENTS` - When too few arguments provided
- `INTERNAL_ERROR` - When an unexpected error occurs

---

## Internal Architecture

The command library is split into a public API and internal implementation:

```
command/
├── Commands.java              # Static facade - register commands here
├── CommandBase.java           # Base class to extend
├── CommandMessage.java        # Error message types
├── CommandMessageManager.java # Customize error messages
├── annotations/               # All annotation definitions
├── permissions/               # Permission handling
├── exceptions/                # Custom exceptions
└── internal/                  # Implementation details
    ├── CommandDefinition      # Immutable command data model
    ├── CommandBuilder         # Fluent builder for definitions
    ├── CommandRegistry        # Manages registration with Bukkit
    ├── CommandExecutor        # Handles command execution
    ├── CommandResolver        # Traverses subcommand tree
    ├── AnnotationParser       # Parses annotations into definitions
    ├── BukkitCommandBridge    # Bridges to Bukkit's command system
    ├── MethodAnalyzer         # Analyzes method parameters
    ├── SyntaxGenerator        # Generates usage syntax
    ├── DebugLogger            # Conditional debug logging
    └── resolver/              # Argument resolution
        ├── ArgumentResolver          # Resolver interface
        ├── ArgumentResolverRegistry  # Manages resolvers
        └── BuiltinResolvers          # Default type resolvers
```

**Flow:**
1. `Commands.register()` passes instances to `CommandRegistry`
2. `AnnotationParser` reads annotations and builds `CommandDefinition` via `CommandBuilder`
3. `BukkitCommandBridge` registers with Bukkit's `CommandMap`
4. On execution, `CommandExecutor` uses `CommandResolver` to find the target subcommand
5. `ArgumentResolverRegistry` converts string arguments to typed parameters
6. The method is invoked via reflection
