package io.github.johnnypixelz.utilizer.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.johnnypixelz.utilizer.provider.Provider;

import java.io.*;

public class Persistence {
    private static Gson gson = new Gson();

    public static <T> T loadFile(String fileName) {
        File file = new File(Provider.getPlugin().getDataFolder().getPath() + File.separator + fileName + ".json");

        try {
            return gson.fromJson(new FileReader(file), new TypeToken<T>(){}.getType());
        } catch (FileNotFoundException ignored) {
            return null;
        }
    }

    public static <T> T loadFile(String fileName, TypeToken<T> token) {
        File file = new File(Provider.getPlugin().getDataFolder().getPath() + File.separator + fileName + ".json");

        try {
            return gson.fromJson(new FileReader(file), token.getType());
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

    public static void setGson(Gson gson) {
        Persistence.gson = gson;
    }
    
}
