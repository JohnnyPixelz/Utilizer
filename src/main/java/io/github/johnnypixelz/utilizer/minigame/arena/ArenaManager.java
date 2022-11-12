package io.github.johnnypixelz.utilizer.minigame.arena;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.johnnypixelz.utilizer.Scheduler;
import io.github.johnnypixelz.utilizer.file.adapters.InterfaceAdapter;
import io.github.johnnypixelz.utilizer.file.storage.Storage;
import io.github.johnnypixelz.utilizer.file.storage.container.file.FileStorageContainer;
import io.github.johnnypixelz.utilizer.gson.typeadapters.BukkitSerializableAdapterFactory;
import io.github.johnnypixelz.utilizer.gson.typeadapters.GsonSerializableAdapterFactory;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ArenaManager<T extends Arena> {
    private final FileStorageContainer<Map<String, Arena>> storageContainer;

    public ArenaManager() {
        this("arenas");
    }

    public ArenaManager(String saveFileName) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Arena.class, new InterfaceAdapter<Arena>())
                .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
                .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
                .serializeNulls()
                .disableHtmlEscaping()
                .create();

        this.storageContainer = Storage.map(String.class, Arena.class)
                .json(saveFileName, gson)
                .container(HashMap::new);
    }

    public HashMap<String, T> getArenas() {
        return (HashMap<String, T>) storageContainer.get();
    }

    public boolean arenaExists(String name) {
        return storageContainer.get().containsKey(name);
    }

    public T getArena(String name) {
        return ((HashMap<String, T>) storageContainer.get()).get(name);
    }

    public void registerArena(String name, T arena) {
        if (arenaExists(name)) return;
        storageContainer.get().put(name, arena);
    }

    public ArenaManager<T> load() {
        storageContainer.load();
        return this;
    }

    public BukkitTask setupAutosave(long seconds) {
        return Scheduler.syncTimer(this::save, seconds * 20L);
    }

    public void save() {
        storageContainer.save();
    }
}
