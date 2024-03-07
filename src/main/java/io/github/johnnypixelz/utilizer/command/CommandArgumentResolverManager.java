package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.exceptions.UnsupportedCommandArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;
import java.util.*;

public class CommandArgumentResolverManager {
    private static final Map<Class<?>, CommandArgumentResolver<CommandArgumentResolverContext, ?>> resolvers = new HashMap<>();

    static {
        registerResolver(Short.class, context -> {
            try {
                return Short.parseShort(context.getArgument());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(short.class, context -> {
            try {
                return Short.parseShort(context.getArgument());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(Integer.class, context -> {
            try {
                return Integer.parseInt(context.getArgument());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(int.class, context -> {
            try {
                return Integer.parseInt(context.getArgument());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(Long.class, context -> {
            try {
                return Long.parseLong(context.getArgument());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(long.class, context -> {
            try {
                return Long.parseLong(context.getArgument());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(Float.class, context -> {
            try {
                return Float.parseFloat(context.getArgument());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(float.class, context -> {
            try {
                return Float.parseFloat(context.getArgument());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(Double.class, context -> {
            try {
                return Double.parseDouble(context.getArgument());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(double.class, context -> {
            try {
                return Double.parseDouble(context.getArgument());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        final List<String> truthValues = Arrays.asList("t", "tr", "tru", "true", "on", "y", "ye", "yes", "1");
        registerResolver(Boolean.class, context -> truthValues.contains(context.getArgument()));
        registerResolver(boolean.class, context -> truthValues.contains(context.getArgument()));
        registerResolver(Character.class, context -> {
            if (context.getArgument().length() > 1) {
                throw new IllegalArgumentException("Argument must be one character");
            }

            return context.getArgument().charAt(0);
        });
        registerResolver(char.class, context -> {
            if (context.getArgument().length() > 1) {
                throw new IllegalArgumentException("Argument must be one character");
            }

            return context.getArgument().charAt(0);
        });
        registerResolver(String.class, CommandArgumentResolverContext::getArgument);
        registerResolver(String[].class, context -> context.getArgument().split(" "));
        registerResolver(Enum.class, context -> {
            final String argument = context.getArgument();
            //noinspection unchecked
            Class<? extends Enum<?>> enumCls = (Class<? extends Enum<?>>) context.getParameter().getType();

            for (Enum<?> enumConstant : enumCls.getEnumConstants()) {
                if (enumConstant.name().equalsIgnoreCase(argument)) {
                    return enumConstant;
                }
            }

            return null;
        });
        registerResolver(Player.class, context -> Bukkit.getPlayer(context.getArgument()));
        registerResolver(OfflinePlayer.class, context -> {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(context.getArgument());

            if (offlinePlayer.isOnline()) return null;
            return offlinePlayer;
        });
    }

    public static <T> void registerResolver(Class<T> type, CommandArgumentResolver<CommandArgumentResolverContext, T> resolver) {
        resolvers.put(type, resolver);
    }

    public static CommandArgumentResolver<CommandArgumentResolverContext, ?> getResolver(Class<?> type) {
        do {
            if (type == Object.class) {
                break;
            }

            final CommandArgumentResolver<CommandArgumentResolverContext, ?> resolver = resolvers.get(type);
            if (resolver != null) {
                return resolver;
            }
        } while ((type = type.getSuperclass()) != null);

        return null;
    }

    public static Object resolve(CommandSender sender, String argument, Parameter parameter) throws UnsupportedCommandArgumentException {
        final CommandArgumentResolver<CommandArgumentResolverContext, ?> resolver = getResolver(parameter.getType());

        if (resolver == null) {
            throw new UnsupportedCommandArgumentException("No resolver exists for type " + parameter.getType().getCanonicalName());
        }

        final CommandArgumentResolverContext commandArgumentResolverContext = new CommandArgumentResolverContext(sender, parameter, argument);
        return resolver.resolve(commandArgumentResolverContext);
    }

}
