package io.github.johnnypixelz.utilizer.command.internal.resolver;

import io.github.johnnypixelz.utilizer.command.exceptions.UnsupportedCommandArgumentException;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for argument resolvers. Manages resolver registration and lookup.
 */
public final class ArgumentResolverRegistry {

    private final Map<Class<?>, ArgumentResolver<?>> resolvers = new HashMap<>();

    public ArgumentResolverRegistry() {
        BuiltinResolvers.registerAll(this);
    }

    /**
     * Registers a resolver for the given type.
     * Automatically registers for the paired primitive/boxed type as well.
     *
     * @param type     the type to register the resolver for
     * @param resolver the resolver
     * @param <T>      the type
     */
    public <T> void register(Class<T> type, ArgumentResolver<T> resolver) {
        resolvers.put(type, resolver);

        // Auto-register paired primitive/boxed type
        Class<?> pairedType = BuiltinResolvers.getPairedType(type);
        if (pairedType != null && !resolvers.containsKey(pairedType)) {
            resolvers.put(pairedType, resolver);
        }
    }

    /**
     * Gets the resolver for the given type, walking up the class hierarchy if needed.
     *
     * @param type the type to get the resolver for
     * @return the resolver, or null if none found
     */
    public ArgumentResolver<?> getResolver(Class<?> type) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            ArgumentResolver<?> resolver = resolvers.get(current);
            if (resolver != null) {
                return resolver;
            }
            current = current.getSuperclass();
        }
        return null;
    }

    /**
     * Checks if a resolver exists for the given type.
     *
     * @param type the type to check
     * @return true if a resolver exists
     */
    public boolean hasResolver(Class<?> type) {
        return getResolver(type) != null;
    }

    /**
     * Resolves an argument to the parameter's type.
     *
     * @param sender    the command sender
     * @param argument  the raw argument string
     * @param parameter the method parameter
     * @return the resolved value
     * @throws UnsupportedCommandArgumentException if no resolver exists for the type
     * @throws ArgumentResolutionException         if resolution fails
     */
    public Object resolve(CommandSender sender, String argument, Parameter parameter)
            throws UnsupportedCommandArgumentException, ArgumentResolutionException {
        ArgumentResolver<?> resolver = getResolver(parameter.getType());

        if (resolver == null) {
            throw new UnsupportedCommandArgumentException(
                    "No resolver registered for type: " + parameter.getType().getCanonicalName()
            );
        }

        ArgumentResolverContext context = new ArgumentResolverContext(sender, parameter, argument);
        return resolver.resolve(context);
    }

}
