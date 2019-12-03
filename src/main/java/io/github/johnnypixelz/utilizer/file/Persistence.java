package io.github.johnnypixelz.utilizer.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.johnnypixelz.utilizer.provider.Provider;

import java.io.*;

public class Persistence {
    private static Gson gson = new Gson();

    public static <T> T loadFile(String fileName, TypeToken typeToken) {
        File file = new File(Provider.getPlugin().getDataFolder().getPath() + File.separator + fileName + ".json");

        try {
            return gson.fromJson(new FileReader(file), typeToken.getType());
        } catch (FileNotFoundException ignored) {
            return null;
        }
    }

    public static boolean saveFile(String fileName, Object object) {
        Provider.getPlugin().getDataFolder().mkdirs();
        try {
            FileWriter writer = new FileWriter(new File(Provider.getPlugin().getDataFolder().getPath() + File.separator + fileName + ".json"));
            gson.toJson(object, writer);
            writer.close();
        } catch (FileNotFoundException ignored) {
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
