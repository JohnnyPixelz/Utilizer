package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.command.annotations.ArgName;
import io.github.johnnypixelz.utilizer.command.annotations.DefaultValue;
import io.github.johnnypixelz.utilizer.command.annotations.Optional;
import net.md_5.bungee.api.ChatColor;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates command syntax/usage strings.
 */
public final class SyntaxGenerator {

    private final String label;
    private final List<String> subLabels;
    private final List<ParameterInfo> parameters;

    private SyntaxGenerator(String label, List<String> subLabels, List<ParameterInfo> parameters) {
        this.label = label;
        this.subLabels = Collections.unmodifiableList(new ArrayList<>(subLabels));
        this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters));
    }

    /**
     * Holds information about a command parameter for syntax generation.
     */
    public static final class ParameterInfo {
        private final String name;
        private final boolean optional;

        public ParameterInfo(String name, boolean optional) {
            this.name = name;
            this.optional = optional;
        }

        public String getName() {
            return name;
        }

        public boolean isOptional() {
            return optional;
        }
    }

    /**
     * Creates a syntax generator for a command.
     *
     * @param definition the command definition
     * @return the syntax generator
     */
    public static SyntaxGenerator forCommand(CommandDefinition definition) {
        List<String> subLabels = new ArrayList<>();
        String label;

        if (definition.getParent() == null) {
            label = definition.getLabels().get(0);
        } else {
            // Build command tree from root to current
            List<CommandDefinition> tree = new ArrayList<>();
            CommandDefinition current = definition;
            while (current != null) {
                tree.add(current);
                current = current.getParent();
            }
            Collections.reverse(tree);

            label = tree.get(0).getLabels().get(0);
            for (int i = 1; i < tree.size(); i++) {
                subLabels.add(tree.get(i).getLabels().get(0));
            }
        }

        List<ParameterInfo> parameters = new ArrayList<>();
        CommandMethodDefinition method = definition.getDefaultMethod();
        if (method != null) {
            for (Parameter param : method.getCommandParameters()) {
                String name = getParameterName(param);
                boolean optional = isOptionalParameter(param);
                parameters.add(new ParameterInfo(name, optional));
            }
        }

        return new SyntaxGenerator(label, subLabels, parameters);
    }

    /**
     * Gets the display name for a parameter.
     * Uses @ArgName if present, otherwise falls back to the actual parameter name.
     *
     * @param param the parameter
     * @return the display name
     */
    private static String getParameterName(Parameter param) {
        ArgName argName = param.getAnnotation(ArgName.class);
        if (argName != null) {
            return argName.value();
        }
        // Fall back to actual parameter name (requires -parameters compiler flag)
        // If not available, this will be something like "arg0", "arg1", etc.
        return param.getName();
    }

    /**
     * Checks if a parameter is optional.
     *
     * @param param the parameter
     * @return true if optional
     */
    private static boolean isOptionalParameter(Parameter param) {
        return param.isAnnotationPresent(Optional.class) || param.isAnnotationPresent(DefaultValue.class);
    }

    /**
     * @return the main command label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the subcommand labels in order
     */
    public List<String> getSubLabels() {
        return subLabels;
    }

    /**
     * @return the parameter info list
     */
    public List<ParameterInfo> getParameters() {
        return parameters;
    }

    /**
     * @return the parameter names (for backwards compatibility)
     */
    public List<String> getParameterNames() {
        return parameters.stream()
                .map(ParameterInfo::getName)
                .toList();  // Returns unmodifiable list
    }

    /**
     * Generates the syntax string with default colors.
     *
     * @return the formatted syntax string
     */
    public String generate() {
        return generate(ChatColor.RED, ChatColor.GOLD, ChatColor.WHITE);
    }

    /**
     * Generates the syntax string with custom colors.
     *
     * @param labelColor     color for the main label
     * @param subLabelColor  color for subcommand labels
     * @param parameterColor color for parameters
     * @return the formatted syntax string
     */
    public String generate(ChatColor labelColor, ChatColor subLabelColor, ChatColor parameterColor) {
        StringBuilder syntax = new StringBuilder();

        syntax.append(labelColor).append("/").append(label);

        if (!subLabels.isEmpty()) {
            syntax.append(" ").append(subLabelColor).append(String.join(" ", subLabels));
        }

        if (!parameters.isEmpty()) {
            String params = parameters.stream()
                    .map(p -> p.isOptional() ? "[" + p.getName() + "]" : "<" + p.getName() + ">")
                    .collect(Collectors.joining(" "));
            syntax.append(" ").append(parameterColor).append(params);
        }

        return syntax.toString();
    }

}
