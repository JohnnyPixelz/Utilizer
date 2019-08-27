package io.github.johnnypixelz.utilizer.message;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {

    public static List<String> splitLines(String bigLine, int maxWords) {
        String[] words = bigLine.split(" ");
        List<String> lore = new ArrayList<>();

        int targetLines = (int) Math.ceil(words.length / (double) maxWords);
        int wordsPerLine = words.length / targetLines;

        String data = "";
        int counter = 0;
        for (String word : words) {
            if (counter == wordsPerLine) {
                data += "\n";
                counter = 0;
            }
            data += word + " ";
            counter++;
        }

        for (String line : data.split("\n")) {
            lore.add(line);
        }

        return lore;
    }
}
