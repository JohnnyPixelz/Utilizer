package io.github.johnnypixelz.utilizer.command;

import java.util.List;

public class CommandSyntax {
    private String label;
    private List<String> subLabels;
    private List<String> parameters;

    public CommandSyntax(Command command) {
        this.label = command.getLabels().get(0);

    }

}
