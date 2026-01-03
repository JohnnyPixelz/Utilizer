package io.github.johnnypixelz.utilizer.command.internal.resolver;

import io.github.johnnypixelz.utilizer.command.internal.FuzzyMatcher;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Provides all built-in argument resolvers for common types.
 */
public final class BuiltinResolvers {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED = Map.of(
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class,
            boolean.class, Boolean.class,
            char.class, Character.class
    );

    private static final List<String> TRUTH_VALUES = List.of(
            "t", "tr", "tru", "true", "on", "y", "ye", "yes", "1"
    );

    private BuiltinResolvers() {
    }

    /**
     * Registers all built-in resolvers with the given registry.
     */
    public static void registerAll(ArgumentResolverRegistry registry) {
        registerNumericResolvers(registry);
        registerBooleanResolver(registry);
        registerCharacterResolver(registry);
        registerStringResolvers(registry);
        registerEnumResolver(registry);
        registerPlayerResolvers(registry);
    }

    private static void registerNumericResolvers(ArgumentResolverRegistry registry) {
        registerNumeric(registry, Short.class, Short::parseShort, "short");
        registerNumeric(registry, Integer.class, Integer::parseInt, "integer");
        registerNumeric(registry, Long.class, Long::parseLong, "long");
        registerNumeric(registry, Float.class, Float::parseFloat, "float");
        registerNumeric(registry, Double.class, Double::parseDouble, "double");
    }

    private static <T extends Number> void registerNumeric(
            ArgumentResolverRegistry registry,
            Class<T> type,
            Function<String, T> parser,
            String typeName) {
        ArgumentResolver<T> resolver = context -> {
            try {
                return parser.apply(context.getArgument());
            } catch (NumberFormatException e) {
                throw new ArgumentResolutionException(
                        "Invalid " + typeName + " value: '" + context.getArgument() + "'"
                );
            }
        };
        registry.register(type, resolver);
    }

    private static void registerBooleanResolver(ArgumentResolverRegistry registry) {
        ArgumentResolver<Boolean> resolver = context ->
                TRUTH_VALUES.contains(context.getArgument().toLowerCase());
        registry.register(Boolean.class, resolver);
    }

    private static void registerCharacterResolver(ArgumentResolverRegistry registry) {
        ArgumentResolver<Character> resolver = context -> {
            String arg = context.getArgument();
            if (arg.length() != 1) {
                throw new ArgumentResolutionException(
                        "Expected single character, got: '" + arg + "'"
                );
            }
            return arg.charAt(0);
        };
        registry.register(Character.class, resolver);
    }

    private static void registerStringResolvers(ArgumentResolverRegistry registry) {
        registry.register(String.class, ArgumentResolverContext::getArgument);
        registry.register(String[].class, context -> context.getArgument().split(" "));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerEnumResolver(ArgumentResolverRegistry registry) {
        ArgumentResolver resolver = context -> {
            String argument = context.getArgument();
            Class<? extends Enum> enumClass = (Class<? extends Enum>) context.getTargetType();

            for (Enum enumConstant : enumClass.getEnumConstants()) {
                if (enumConstant.name().equalsIgnoreCase(argument)) {
                    return enumConstant;
                }
            }

            // Try fuzzy matching for suggestion
            List<String> enumNames = Arrays.stream(enumClass.getEnumConstants())
                    .map(e -> ((Enum<?>) e).name().toLowerCase())
                    .collect(Collectors.toList());

            Optional<String> suggestion = FuzzyMatcher.findClosest(enumNames, argument);
            String message = "Invalid value '" + argument + "' for " + enumClass.getSimpleName();
            if (suggestion.isPresent()) {
                message += ". Did you mean '" + suggestion.get() + "'?";
            }
            throw new ArgumentResolutionException(message);
        };
        registry.register(Enum.class, resolver);
    }

    private static void registerPlayerResolvers(ArgumentResolverRegistry registry) {
        ArgumentResolver<Player> playerResolver = context -> {
            String input = context.getArgument();
            Player player = Bukkit.getPlayer(input);
            if (player == null) {
                // Try fuzzy matching for suggestion
                List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList());

                Optional<String> suggestion = FuzzyMatcher.findClosest(playerNames, input);
                String message = "Player not found: '" + input + "'";
                if (suggestion.isPresent()) {
                    message += ". Did you mean '" + suggestion.get() + "'?";
                }
                throw new ArgumentResolutionException(message);
            }
            return player;
        };
        registry.register(Player.class, playerResolver);

        ArgumentResolver<OfflinePlayer> offlinePlayerResolver = context -> {
            @SuppressWarnings("deprecation")
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(context.getArgument());
            return offlinePlayer;
        };
        registry.register(OfflinePlayer.class, offlinePlayerResolver);
    }

    /**
     * Returns the paired primitive/boxed type, or null if not a primitive/boxed type.
     */
    public static Class<?> getPairedType(Class<?> type) {
        if (type.isPrimitive()) {
            return PRIMITIVE_TO_BOXED.get(type);
        }
        return PRIMITIVE_TO_BOXED.entrySet().stream()
                .filter(entry -> entry.getValue().equals(type))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

}
