package io.github.johnnypixelz.utilizer.command.exceptions;

public class CommandAnnotationParseException extends Exception {

    public CommandAnnotationParseException() {
        super();
    }

    public CommandAnnotationParseException(String message) {
        super(message);
    }

    public CommandAnnotationParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandAnnotationParseException(Throwable cause) {
        super(cause);
    }

}
