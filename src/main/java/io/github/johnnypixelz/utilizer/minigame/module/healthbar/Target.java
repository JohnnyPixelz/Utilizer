package io.github.johnnypixelz.utilizer.minigame.module.healthbar;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Target {
    private Player target;
    private BukkitTask actionBarTask;

    public Target(Player target, BukkitTask actionBarTask) {
        this.target = target;
        this.actionBarTask = actionBarTask;
    }


}
