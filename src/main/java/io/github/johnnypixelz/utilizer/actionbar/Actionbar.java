package io.github.johnnypixelz.utilizer.actionbar;

import io.github.johnnypixelz.utilizer.reflection.Reflection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Actionbar {
    private static Constructor<?> actionbarConstructor;
    private static Method a;

    static {
        try {
            actionbarConstructor = Reflection.getNMSClass("PacketPlayOutChat").getConstructor(Reflection.getNMSClass("IChatBaseComponent"), byte.class);
            a = Reflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class);
        } catch (Exception e) {
            System.out.println("Could not initialize");
            e.printStackTrace();
        }
    }

    public static void sendActionbar(Player player, String text) {
        try {
            Object chatTitle = a.invoke(null, ChatColor.translateAlternateColorCodes('&', "{\"text\": \"" + text + "\"}"));
            Object packet = actionbarConstructor.newInstance(chatTitle, (byte) 2);
            Reflection.sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
            //Do something
        }
    }
}
