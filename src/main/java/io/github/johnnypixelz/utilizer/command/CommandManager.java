package io.github.johnnypixelz.utilizer.command;

import org.bukkit.entity.Player;

public class CommandManager {

    public static void test() {
        final Command command = Commands.builder("help")
                .setDescription("Help command")
                .addSubcommand(Commands.builder("guilds")
                        .setDescription("Displays help information about guilds")
                        .build())
                .setExecution(Player.class, String.class, (player, s) -> {

                })
                .build();


    }

}
