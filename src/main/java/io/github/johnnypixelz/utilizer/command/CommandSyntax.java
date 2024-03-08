package io.github.johnnypixelz.utilizer.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandSyntax {
    private String label;
    private final List<String> subLabels;
    private final List<String> parameters;

    public CommandSyntax(Command command) {
        this.subLabels = new ArrayList<>();
        this.parameters = new ArrayList<>();

        if (command.getParent() == null) {
            this.label = command.getLabels().get(0);
        } else {
            List<Command> commandTree = new ArrayList<>();
            commandTree.add(command);

            Command firstCommand = command;
            while (firstCommand.getParent() != null) {
                firstCommand = firstCommand.getParent();
                commandTree.add(firstCommand);
            }

            Collections.reverse(commandTree);

            for (int i = 0; i < commandTree.size(); i++) {
                final Command reversedCommandTreeCommand = commandTree.get(i);
                if (i == 0) {
                    this.label = reversedCommandTreeCommand.getLabels().get(0);
                    continue;
                }

                final String subLabel = reversedCommandTreeCommand.getLabels().get(0);
                this.subLabels.add(subLabel);
            }
        }

        Parameter[] methodParameters = command.getDefaultMethod().getMethod().getParameters();

        if (methodParameters.length > 0) {
            final Class<?> firstParameterType = methodParameters[0].getType();

            //            Checking if the parameter is a command sender
            if (CommandSender.class.isAssignableFrom(firstParameterType) || CommandSender.class.isAssignableFrom(firstParameterType.getSuperclass())) {
                final Parameter[] newMethodParameters = new Parameter[methodParameters.length - 1];
                System.arraycopy(methodParameters, 1, newMethodParameters, 0, newMethodParameters.length);
                methodParameters = newMethodParameters;
            }
        }

        for (Parameter methodParameter : methodParameters) {
            this.parameters.add(methodParameter.getName());
        }
    }

    public String getLabel() {
        return label;
    }

    public List<String> getSubLabels() {
        return subLabels;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String generateSyntax() {
        return generateSyntax(ChatColor.RED, ChatColor.GOLD, ChatColor.WHITE);
    }

    public String generateSyntax(ChatColor labelColor, ChatColor subLabelColor, ChatColor parameterColor) {
        final String subLabelsFormatted = String.join(" ", subLabels);
        final String parametersFormatted = parameters.stream().map(parameter -> "<" + parameter + ">").collect(Collectors.joining(" "));
        return labelColor + "/" + label + " " + subLabelColor + subLabelsFormatted + " " + parameterColor + parametersFormatted;
    }

}
