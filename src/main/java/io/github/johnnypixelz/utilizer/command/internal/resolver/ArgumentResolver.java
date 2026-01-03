package io.github.johnnypixelz.utilizer.command.internal.resolver;

/**
 * Resolves a command argument string into a typed object.
 *
 * @param <T> the type this resolver produces
 */
@FunctionalInterface
public interface ArgumentResolver<T> {

    /**
     * Resolves the argument from the given context.
     *
     * @param context the resolution context containing sender, argument string, and parameter info
     * @return the resolved value
     * @throws ArgumentResolutionException if the argument cannot be resolved
     */
    T resolve(ArgumentResolverContext context) throws ArgumentResolutionException;

}
