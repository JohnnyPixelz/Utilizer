package io.github.johnnypixelz.utilizer.command;

public class Commands {

    public static CommandBuilder builder(String root) {
        return new CommandBuilder(root);
    }

}
