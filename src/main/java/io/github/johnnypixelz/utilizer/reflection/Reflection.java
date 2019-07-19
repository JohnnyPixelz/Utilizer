package io.github.johnnypixelz.utilizer.reflection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {
    private static Class Packet;
    private static Class CraftPlayer;
    private static Class EntityPlayer;
    private static Method getHandle;
    private static Method sendPacket;
    private static Field playerConnection;

    static {
        try {
            Packet = getNMSClass("Packet");
            CraftPlayer = getCraftBukkitClass("entity.CraftPlayer");
            EntityPlayer = getNMSClass("EntityPlayer");
            getHandle = CraftPlayer.getMethod("getHandle");
            sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", Packet);
            playerConnection = EntityPlayer.getField("playerConnection");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get NMS class using reflection
     * @param name Name of the class
     * @return Class
     */
    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        }
        catch(ClassNotFoundException ex) {
            ex.printStackTrace();
            //Do something
        }
        return null;
    }

    public static Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
        } catch(ClassNotFoundException ex) {
            ex.printStackTrace();
            //Do something
        }
        return null;
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            sendPacket.invoke(playerConnection.get(getHandle.invoke(player)), packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
