package io.github.johnnypixelz.utilizer.command.exceptions;

public class UnsupportedCommandArgumentException extends Exception {

    public UnsupportedCommandArgumentException() {
        super();
    }

    public UnsupportedCommandArgumentException(String message) {
        super(message);
    }

    public UnsupportedCommandArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedCommandArgumentException(Throwable cause) {
        super(cause);
    }

}
