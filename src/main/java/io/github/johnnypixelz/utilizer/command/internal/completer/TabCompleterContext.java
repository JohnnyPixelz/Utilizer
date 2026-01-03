package io.github.johnnypixelz.utilizer.command.internal.completer;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Context provided to tab completers during completion.
 */
public final class TabCompleterContext {

    private final CommandSender sender;
    private final Parameter parameter;
    private final String partial;
    private final List<String> previousArgs;
    private final String config;  // Optional config data from @range:1-100

    public TabCompleterContext(CommandSender sender, Parameter parameter, String partial, List<String> previousArgs, String config) {
        this.sender = sender;
        this.parameter = parameter;
        this.partial = partial;
        this.previousArgs = previousArgs;
        this.config = config;
    }

    /**
     * @return the command sender requesting completion
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * @return the parameter being completed (may be null for dynamic completions)
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * @return the partial input being completed (may be empty string)
     */
    public String getPartial() {
        return partial;
    }

    /**
     * @return the arguments before the current one
     */
    public List<String> getPreviousArgs() {
        return previousArgs;
    }

    /**
     * @return optional configuration string (e.g., "1-100" from "@range:1-100")
     */
    public String getConfig() {
        return config;
    }

    /**
     * @return true if the partial input is empty
     */
    public boolean isEmpty() {
        return partial.isEmpty();
    }

    /**
     * Filters a suggestion based on partial match.
     *
     * @param suggestion the suggestion to check
     * @return true if the suggestion starts with the partial (case-insensitive)
     */
    public boolean matches(String suggestion) {
        return suggestion.toLowerCase().startsWith(partial.toLowerCase());
    }

}
