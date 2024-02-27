package io.github.johnnypixelz.utilizer.command;

import java.util.Arrays;
import java.util.List;

public class CommandUtil {

    public static List<String> parseLabel(String label) {
        return Arrays.stream(label.split("\\|"))
                .map(String::trim)
                .toList();
    }

}
