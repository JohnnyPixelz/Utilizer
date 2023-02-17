package io.github.johnnypixelz.utilizer.itemstack;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.UUID;

public class Skulls {
    private static Method metaSetProfileMethod;
    private static Field metaProfileField;

    @NotNull
    public static ItemStack createSkull() {
        return new ItemStack(Material.PLAYER_HEAD);
    }

    @NotNull
    public static ItemStack getSkull(@NotNull String identifier) {
        final ItemStack skull = createSkull();

        if (isUsername(identifier)) return mutateSkullFromName(skull, identifier);
        if (identifier.contains("https://")) return mutateSkullFromURL(skull, identifier);
        if (identifier.length() > 100 && isBase64(identifier)) return mutateSkullFromBase64(skull, identifier);
        return skull;
    }

    @NotNull
    public static ItemStack mutateSkull(@NotNull ItemStack skull, @NotNull String identifier) {
        if (isUsername(identifier)) return mutateSkullFromName(skull, identifier);
        if (identifier.contains("https://")) return mutateSkullFromURL(skull, identifier);
        if (identifier.length() > 100 && isBase64(identifier)) return mutateSkullFromBase64(skull, identifier);
        return skull;
    }

    @NotNull
    public static ItemStack getSkullFromUUID(@NotNull UUID uuid) {
        return mutateSkullFromUUID(createSkull(), uuid);
    }

    @NotNull
    public static ItemStack mutateSkullFromUUID(@NotNull ItemStack skull, @NotNull UUID uuid) {
        if (!(skull.getItemMeta() instanceof SkullMeta meta)) {
            throw new IllegalArgumentException("Skull must be of type PLAYER_HEAD");
        }

        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        skull.setItemMeta(meta);

        return skull;
    }

    @NotNull
    public static ItemStack getSkullFromName(@NotNull String name) {
        return mutateSkullFromName(createSkull(), name);
    }

    @NotNull
    public static ItemStack mutateSkullFromName(@NotNull ItemStack skull, @NotNull String name) {
        if (!(skull.getItemMeta() instanceof SkullMeta meta)) {
            throw new IllegalArgumentException("Skull must be of type PLAYER_HEAD");
        }

        meta.setOwner(name);
        skull.setItemMeta(meta);

        return skull;
    }

    @NotNull
    public static ItemStack getSkullFromURL(@NotNull String url) {
        return mutateSkullFromURL(createSkull(), url);
    }

    @NotNull
    public static ItemStack mutateSkullFromURL(@NotNull ItemStack skull, @NotNull String url) {
        return mutateSkullFromBase64(skull, urlToBase64(url));
    }

    @NotNull
    public static ItemStack getSkullFromBase64(@NotNull String base64) {
        return mutateSkullFromBase64(createSkull(), base64);
    }

    @NotNull
    public static ItemStack mutateSkullFromBase64(@NotNull ItemStack skull, @NotNull String base64) {
        if (!(skull.getItemMeta() instanceof SkullMeta meta)) {
            throw new IllegalArgumentException("Skull must be of type PLAYER_HEAD");
        }

        mutateItemMeta(meta, base64);
        skull.setItemMeta(meta);

        return skull;
    }

    @NotNull
    private static String urlToBase64(@NotNull String url) {
        URI actualUrl;

        try {
            actualUrl = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + actualUrl + "\"}}}";
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }

    private static void mutateItemMeta(@NotNull SkullMeta meta, @NotNull String b64) {
        try {
            if (metaSetProfileMethod == null) {
                metaSetProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                metaSetProfileMethod.setAccessible(true);
            }
            metaSetProfileMethod.invoke(meta, makeProfile(b64));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            // if in an older API where there is no setProfile method,
            // we set the profile field directly.
            try {
                if (metaProfileField == null) {
                    metaProfileField = meta.getClass().getDeclaredField("profile");
                    metaProfileField.setAccessible(true);
                }
                metaProfileField.set(meta, makeProfile(b64));

            } catch (NoSuchFieldException | IllegalAccessException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    @NotNull
    private static GameProfile makeProfile(@NotNull String b64) {
        // random uuid based on the b64 string
        UUID id = new UUID(
                b64.substring(b64.length() - 20).hashCode(),
                b64.substring(b64.length() - 10).hashCode()
        );

        GameProfile profile = new GameProfile(id, "Player");
        profile.getProperties().put("textures", new Property("textures", b64));
        return profile;
    }

    /**
     * While RegEx is a little faster for small strings, this always checks strings with a length
     * greater than 100, so it'll perform a lot better.
     */
    private static boolean isBase64(@NotNull String base64) {
        try {
            Base64.getDecoder().decode(base64);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    /**
     * https://help.minecraft.net/hc/en-us/articles/360034636712
     *
     * @param name the username to check.
     *
     * @return true if the string matches the Minecraft username rule, otherwise false.
     */
    private static boolean isUsername(@NotNull String name) {
        int len = name.length();
        if (len < 3 || len > 16) return false;

        // For some reasons Apache's Lists.charactersOf is faster than character indexing for small strings.
        for (char ch : Lists.charactersOf(name)) {
            if (ch != '_' && !(ch >= 'A' && ch <= 'Z') && !(ch >= 'a' && ch <= 'z') && !(ch >= '0' && ch <= '9')) return false;
        }
        return true;
    }

}
