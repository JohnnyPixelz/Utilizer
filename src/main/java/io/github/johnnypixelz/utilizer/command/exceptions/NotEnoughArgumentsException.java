package io.github.johnnypixelz.utilizer.command.exceptions;

public class NotEnoughArgumentsException extends Exception {

    public NotEnoughArgumentsException() {
        super();
    }

    public NotEnoughArgumentsException(String message) {
        super(message);
    }

    public NotEnoughArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughArgumentsException(Throwable cause) {
        super(cause);
    }

}
