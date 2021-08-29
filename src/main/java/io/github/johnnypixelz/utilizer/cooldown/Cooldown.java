package io.github.johnnypixelz.utilizer.cooldown;

import org.bukkit.event.Listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cooldown<T> implements Listener {
    protected Map<T, Long> cooldownMap = new ConcurrentHashMap<>();

    public void set(T object, Long ms) {
        cooldownMap.put(object, System.currentTimeMillis() + ms);
    }

    public void add(T object, Long ms) {
        if (isOnCooldown(object)) {
            cooldownMap.put(object, cooldownMap.get(object) + ms);
        } else {
            set(object, ms);
        }
    }

    public void remove(T object) {
        cooldownMap.remove(object);
    }

    public boolean isOnCooldown(T object) {
        if (!cooldownMap.containsKey(object)) return false;
        return cooldownMap.get(object) > System.currentTimeMillis();
    }

    public long getCooldown(T object) {
        return cooldownMap.get(object);
    }

    public Map<T, Long> getCooldownMap() {
        return cooldownMap;
    }
}
