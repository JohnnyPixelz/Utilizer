package io.github.johnnypixelz.utilizer.title;

import io.github.johnnypixelz.utilizer.reflection.Reflection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Title {
    private static Constructor<?> titleConstructor;
    private static Method a;
    private static Object enumTitle;
    private static Object enumSubtitle;

    static {
        try {
            a = Reflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class);
            titleConstructor = Reflection.getNMSClass("PacketPlayOutTitle").getConstructor(Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], Reflection.getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            enumTitle = Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
            enumSubtitle = Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("Could not initialize");
        }
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
        try {
            Object chatTitle = a.invoke(null, ChatColor.translateAlternateColorCodes('&', "{\"text\": \"" + title + "\"}"));
            Object titlePacket = titleConstructor.newInstance(enumTitle, chatTitle, fadeInTime, showTime, fadeOutTime);

            Reflection.sendPacket(player, titlePacket);

            if (subtitle != null) {
                Object chatSubtitle = a.invoke(null, ChatColor.translateAlternateColorCodes('&', "{\"text\": \"" + subtitle + "\"}"));
                Object subtitlePacket = titleConstructor.newInstance(enumSubtitle, chatSubtitle, fadeInTime, showTime, fadeOutTime);
                Reflection.sendPacket(player, subtitlePacket);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            //Do something
        }
    }

    public static void sendTitle(Player player, String title) {
        sendTitle(player, title, null, 10, 70, 20);
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 10, 70, 20);
    }

    public static void sendTitle(Player player, String title, int fadeInTime, int showTime, int fadeOutTime) {
        sendTitle(player, title, null, fadeInTime, showTime, fadeOutTime);
    }

}
