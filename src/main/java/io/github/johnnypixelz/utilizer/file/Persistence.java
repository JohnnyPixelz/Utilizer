package io.github.johnnypixelz.utilizer.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.reflect.Type;

public class Persistence {
    private static Gson gson = new Gson();

    public static <T> T loadFile(@Nonnull File file) {
        return loadFile(file, null);
    }

    public static <T> T loadFile(@Nonnull File file, @Nullable TypeToken<T> token) {
        try {
            Type type = token != null
                    ? token.getType()
                    : new TypeToken<T>(){}.getType();
            return gson.fromJson(new FileReader(file), type);
        } catch (FileNotFoundException ignored) {
            return null;
        }
    }

    public static <T> T loadFile(@Nonnull String fileName) {
        File file = new File(Provider.getPlugin().getDataFolder().getPath() + File.separator + fileName + ".json");
        return loadFile(file);
    }

    public static <T> T loadFile(@Nonnull String fileName, @Nullable TypeToken<T> token) {
        File file = new File(Provider.getPlugin().getDataFolder().getPath() + File.separator + fileName + ".json");
        return loadFile(file, token);
    }

    public static boolean saveFile(@Nonnull File file, @Nonnull Object object) {
        return saveFile(file, object, null);
    }

    public static boolean saveFile(@Nonnull String fileName, @Nonnull Object object) {
        Provider.getPlugin().getDataFolder().mkdirs();
        File file = new File(Provider.getPlugin().getDataFolder().getPath() + File.separator + fileName + ".json");
        return saveFile(file, object);
    }

    public static <T> boolean saveFile(@Nonnull File file, @Nonnull Object object, @Nullable TypeToken<T> token) {
        try {
            FileWriter writer = new FileWriter(file);
            Type type = token != null
                    ? token.getType()
                    : new TypeToken<T>(){}.getType();
            gson.toJson(object, type, writer);
            writer.close();
        } catch (FileNotFoundException ignored) {
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static <T> boolean saveFile(@Nonnull String fileName, @Nonnull Object object, @Nullable TypeToken<T> token) {
        Provider.getPlugin().getDataFolder().mkdirs();
        File file = new File(Provider.getPlugin().getDataFolder().getPath() + File.separator + fileName + ".json");
        return saveFile(file, object, token);
    }

    public static void setGson(Gson gson) {
        Persistence.gson = gson;
    }

    @Nonnull
    public static Gson getGson() {
        return gson;
    }

}
