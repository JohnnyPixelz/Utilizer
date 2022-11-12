package io.github.johnnypixelz.utilizer.file;

import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.function.Consumer;

public class FileWatcher extends BukkitRunnable {
    private long originalModified;
    private final File file;
    private final StatefulEventEmitter<File> onSave;

    private FileWatcher(File file, Consumer<File> callback) {
        this.file = file;
        this.originalModified = file.lastModified();
        this.onSave = new StatefulEventEmitter<>();
        onSave.listen(callback);
    }

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
            Bukkit.getScheduler().runTask(Provider.getPlugin(), () -> onSave.emit(file));
        }
    }
}
