package io.github.johnnypixelz.utilizer.command.exceptions;

/**
 * Thrown when a command is executed with fewer arguments than required.
 */
public class NotEnoughArgumentsException extends Exception {

    private final int required;
    private final int provided;

    public NotEnoughArgumentsException(int required, int provided) {
        super("Expected " + required + " argument(s) but received " + provided);
        this.required = required;
        this.provided = provided;
    }

    public NotEnoughArgumentsException(String message) {
        super(message);
        this.required = -1;
        this.provided = -1;
    }

    /**
     * @return the number of required arguments, or -1 if unknown
     */
    public int getRequired() {
        return required;
    }

    /**
     * @return the number of provided arguments, or -1 if unknown
     */
    public int getProvided() {
        return provided;
    }

}
