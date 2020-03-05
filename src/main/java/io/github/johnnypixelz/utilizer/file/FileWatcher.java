package io.github.johnnypixelz.utilizer.file;

import io.github.johnnypixelz.utilizer.provider.Provider;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.function.Consumer;

public class FileWatcher extends BukkitRunnable {
    private long originalModified;
    private File file;
    private Consumer<File> callback;

    private FileWatcher(File file, Consumer<File> callback) {
        this.file = file;
        originalModified = file.lastModified();
        this.callback = callback;
    }

    private FileWatcher() {}

    public static FileWatcher watchFile(File file, Consumer<File> callback) {
        FileWatcher watcher = new FileWatcher(file, callback);
        watcher.runTaskTimerAsynchronously(Provider.getPlugin(), 20L, 20L);
        return watcher;
    }

    public static FileWatcher watchPluginFile(String fileName, Consumer<File> callback) {
        FileWatcher watcher = new FileWatcher(new File(Provider.getPlugin().getDataFolder().getPath() + File.separator + fileName), callback);
        watcher.runTaskTimerAsynchronously(Provider.getPlugin(), 20L, 20L);
        return watcher;
    }

    @Override
    public void run() {
        if (originalModified != file.lastModified()) {
            originalModified = file.lastModified();
            Bukkit.getScheduler().runTask(Provider.getPlugin(), () -> callback.accept(file));
        }
    }
}
