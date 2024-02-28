package io.github.johnnypixelz.utilizer.command;

import io.github.johnnypixelz.utilizer.command.annotations.Permission;
import io.github.johnnypixelz.utilizer.command.permissions.CommandPermission;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandUtil {

    public static List<String> parseLabel(String label) {
        return Arrays.stream(label.split("\\|"))
                .map(String::trim)
                .toList();
    }

}
