package io.github.johnnypixelz.utilizer.cooldown;

import io.github.johnnypixelz.utilizer.provider.Provider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Cooldown implements Listener {
    protected Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public Cooldown() {
        Bukkit.getPluginManager().registerEvents(this, Provider.getPlugin());
    }

    public void set(Player player, Long ms) {
        set(player.getUniqueId(), ms);
    }

    public void set(UUID uuid, Long ms) {
        cooldowns.put(uuid, System.currentTimeMillis() + ms);
    }

    public void add(Player player, Long ms) {
        add(player.getUniqueId(), ms);
    }

    public void add(UUID uuid, Long ms) {
        if (isOnCooldown(uuid)) {
            cooldowns.put(uuid, cooldowns.get(uuid) + ms);
        } else {
            set(uuid, ms);
        }
    }

    public boolean isOnCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid)) return false;
        return cooldowns.get(uuid) > System.currentTimeMillis();
    }

    public boolean isOnCooldown(Player player) {
        return isOnCooldown(player.getUniqueId());
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer().getUniqueId());
    }
}
