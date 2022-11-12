package io.github.johnnypixelz.utilizer.maven;

import com.google.common.base.Suppliers;
import io.github.johnnypixelz.utilizer.plugin.Logs;
import io.github.johnnypixelz.utilizer.plugin.Provider;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DependencyLoader {
    private static final Supplier<URLClassLoaderAccess> URL_INJECTOR = Suppliers.memoize(() -> URLClassLoaderAccess.create((URLClassLoader) Provider.getPlugin().getClass().getClassLoader()));
    private static final List<Dependency> loadedDependencies = new ArrayList<>();

    public static void load(Dependency dependency) {
        if (loadedDependencies.contains(dependency)) return;

        Logs.info(String.format("Loading dependency %s:%s:%s from %s", dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getRepoUrl()));

        String fileName = dependency.getArtifactId() + "-" + dependency.getVersion();

        File saveLocation = new File(getLibFolder(), fileName + ".jar");
        if (!saveLocation.exists()) {

            try {
                Logs.info("Dependency '" + fileName + "' is not already in the libraries folder. Attempting to download...");
                URL url = dependency.getUrl();

                try (InputStream is = url.openStream()) {
                    Files.copy(is, saveLocation.toPath());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            Logs.info("Dependency '" + fileName + "' successfully downloaded.");
        }

        if (!saveLocation.exists()) {
            throw new RuntimeException("Unable to download dependency: " + dependency);
        }

        try {
            URL_INJECTOR.get().addURL(saveLocation.toURI().toURL());
        } catch (Exception e) {
            throw new RuntimeException("Unable to load dependency: " + saveLocation, e);
        }

        Logs.info("Loaded dependency '" + fileName + "' successfully.");
        loadedDependencies.add(dependency);
    }

    private static File getLibFolder() {
        final File dataFolder = Provider.getPlugin().getDataFolder();
        final File pluginFolder = dataFolder.getParentFile();

        final File libsFolder = new File(pluginFolder, ".libs");
        libsFolder.mkdirs();
        return libsFolder;
    }

}
