package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.exceptions.UnsupportedCommandArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

public class CommandArgumentResolverManager {
    private static final Map<Class<?>, CommandArgumentResolver<String, ?>> resolvers = new HashMap<>();

    static {
        registerResolver(Short.class, argument -> {
            try {
                return Short.parseShort(argument);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(short.class, argument -> {
            try {
                return Short.parseShort(argument);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(Integer.class, argument -> {
            try {
                return Integer.parseInt(argument);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(int.class, argument -> {
            try {
                return Integer.parseInt(argument);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(Long.class, argument -> {
            try {
                return Long.parseLong(argument);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(long.class, argument -> {
            try {
                return Long.parseLong(argument);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(Float.class, argument -> {
            try {
                return Float.parseFloat(argument);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(float.class, argument -> {
            try {
                return Float.parseFloat(argument);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(Double.class, argument -> {
            try {
                return Double.parseDouble(argument);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        registerResolver(double.class, argument -> {
            try {
                return Double.parseDouble(argument);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("");
            }
        });
        final List<String> truthValues = Arrays.asList("t", "tr", "tru", "true", "on", "y", "ye", "yes", "1");
        registerResolver(Boolean.class, truthValues::contains);
        registerResolver(boolean.class, truthValues::contains);
        registerResolver(Character.class, argument -> {
            if (argument.length() > 1) {
                throw new IllegalArgumentException("Argument must be one character");
            }

            return argument.charAt(0);
        });
        registerResolver(char.class, argument -> {
            if (argument.length() > 1) {
                throw new IllegalArgumentException("Argument must be one character");
            }

            return argument.charAt(0);
        });
        registerResolver(String[].class, argument -> argument.split(" "));
//        registerResolver(Enum.class, argument -> ); TODO implement enums
        registerResolver(Player.class, Bukkit::getPlayer);
        registerResolver(OfflinePlayer.class, argument -> {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(argument);

            if (offlinePlayer.isOnline()) return null;
            return offlinePlayer;
        });
    }

    public static <T> void registerResolver(Class<T> type, CommandArgumentResolver<String, T> resolver) {
        resolvers.put(type, resolver);
    }

    public static CommandArgumentResolver<String, ?> getResolver(Class<?> type) {
        do {
            if (type == Object.class) {
                break;
            }

            final CommandArgumentResolver<String, ?> resolver = resolvers.get(type);
            if (resolver != null) {
                return resolver;
            }
        } while ((type = type.getSuperclass()) != null);

        return null;
    }

    public static Object resolve(Class<?> type, String argument) throws UnsupportedCommandArgumentException {
        final CommandArgumentResolver<String, ?> resolver = getResolver(type);

        if (resolver == null) {
            throw new UnsupportedCommandArgumentException("No resolver exists for type " + type.getCanonicalName());
        }

        return resolver.resolve(argument);
    }

}
