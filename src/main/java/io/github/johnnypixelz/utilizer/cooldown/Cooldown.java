package io.github.johnnypixelz.utilizer.cooldown;

import org.bukkit.event.Listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cooldown<T> implements Listener {
    protected Map<T, Long> cooldowns = new ConcurrentHashMap<>();

    public void set(T t, Long ms) {
        cooldowns.put(t, System.currentTimeMillis() + ms);
    }

    public void add(T t, Long ms) {
        if (isOnCooldown(t)) {
            cooldowns.put(t, cooldowns.get(t) + ms);
        } else {
            set(t, ms);
        }
    }

    public void remove(T t) {
        cooldowns.remove(t);
    }

    public boolean isOnCooldown(T t) {
        if (!cooldowns.containsKey(t)) return false;
        return cooldowns.get(t) > System.currentTimeMillis();
    }

    public long getCooldown(T t) {
        return cooldowns.get(t);
    }
}
