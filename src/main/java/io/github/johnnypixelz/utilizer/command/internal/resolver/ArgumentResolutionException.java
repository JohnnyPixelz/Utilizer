package io.github.johnnypixelz.utilizer.command.internal.resolver;

/**
 * Thrown when an argument cannot be resolved to the expected type.
 */
public class ArgumentResolutionException extends Exception {

    public ArgumentResolutionException(String message) {
        super(message);
    }

    public ArgumentResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
