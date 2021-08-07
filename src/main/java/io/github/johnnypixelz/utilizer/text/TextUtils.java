package io.github.johnnypixelz.utilizer.text;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {

    public static List<String> splitLines(String bigLine, int maxWords) {
        String[] words = bigLine.split(" ");
        List<String> lore = new ArrayList<>();

        int targetLines = (int) Math.ceil(words.length / (double) maxWords);
        int wordsPerLine = words.length / targetLines;

        StringBuilder data = new StringBuilder();
        int counter = 0;
        for (String word : words) {
            if (counter == wordsPerLine) {
                data.append("\n");
                counter = 0;
            }
            data.append(word).append(" ");
            counter++;
        }

        for (String line : data.toString().split("\n")) {
            lore.add(line);
        }

        return lore;
    }
}
