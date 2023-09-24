package io.github.johnnypixelz.utilizer.reflection;

import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Reflection {

    /**
     * Get NMS class using reflection
     *
     * @param name Name of the class
     * @return Class
     */
    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            //Do something
        }
        return null;
    }

    public static Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            //Do something
        }
        return null;
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    /**
     * Gets a new instance from the provided class
     *
     * @param clazz The class
     * @param <T>   The type of the class
     * @return The new instance of the class, or null if error
     */
    public static <T> T getInstanceFromClass(@Nonnull Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Gets all non-abstract, non-interface classes which extend a certain class within a plugin
     *
     * @param clazz The class
     * @param <T>   The type of the class
     * @return The list of matching classes
     */
    public static <T> List<Class<T>> getExtendingClasses(@Nonnull Class<T> clazz) {
        List<Class<T>> list = new ArrayList<>();
        try {
            ClassLoader loader = Provider.getPlugin().getClass().getClassLoader();
            JarFile file = new JarFile(new File(Provider.getPlugin().getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
            Enumeration<JarEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName();
                if (!name.endsWith(".class")) {
                    continue;
                }
                name = name.substring(0, name.length() - 6).replace("/", ".");
                Class<?> c;
                try {
                    c = Class.forName(name, true, loader);
                } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                    continue;
                }
                if (!clazz.isAssignableFrom(c) || Modifier.isAbstract(c.getModifiers()) || c.isInterface()) {
                    continue;
                }
                list.add((Class<T>) c);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return list;
    }

    public static <T> List<T> getInstantiatedExtendingClasses(@Nonnull Class<T> clazz) {
        List<T> list = new ArrayList<>();

        for (Class<T> extendingClass : getExtendingClasses(clazz)) {
            try {
                Constructor<?> constructor = extendingClass.getConstructor();
                T instance = (T) constructor.newInstance();
                list.add(instance);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Class " + extendingClass.getName() + " does not have a default constructor or could not be loaded", e);
            }
        }

        return list;
    }
}
