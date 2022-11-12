package io.github.johnnypixelz.utilizer.minigame.module.healthbar;

import com.cryptomorin.xseries.messages.ActionBar;
import io.github.johnnypixelz.utilizer.Scheduler;
import io.github.johnnypixelz.utilizer.cooldown.DynamicPlayerCooldown;
import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import io.github.johnnypixelz.utilizer.text.Colors;
import io.github.johnnypixelz.utilizer.text.Symbols;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HealthBarModule extends MinigameModule {
    private final DynamicPlayerCooldown healthBar = new DynamicPlayerCooldown();
    private final Map<Player, Player> targetMap = new HashMap<>();
    private final int healthBarDuration;
    private String deathMessage = null;

    public HealthBarModule() {
        this(10);
    }

    public HealthBarModule(int healthBarDuration) {
        this.healthBarDuration = healthBarDuration;
    }

    /**
     * @param message the message that will be sent to the targeter's
     *                actionbar if the target dies.
     *                <p>
     *                Placeholders - %killer%, %target%
     * @return own instance as a way to implement method chaining
     */
    public HealthBarModule setDeathMessage(String message) {
        deathMessage = message;
        return this;
    }

    @Override
    protected void init() {
        healthBar.setOnDone(targetMap::remove);

        BukkitTask healthBarTask = Scheduler.syncTimer(() -> {
            targetMap.forEach(this::showHealthBar);
        }, 3);

        getEventManager().getOnMinigameFinish().listen(() -> {
            healthBarTask.cancel();
            targetMap.keySet().forEach(ActionBar::clearActionBar);
            targetMap.clear();
        });

        getEventManager().getOnPlayerRemove().listen(player -> {
            getTargetedBy(player).ifPresent(this::clearTarget);
        });
    }

    private void showHealthBar(Player player, Player target) {
        final String playerHealthBar = Symbols.getPlayerHealthBar(target);
        ActionBar.sendActionBar(player, Colors.color(playerHealthBar));
    }

    private Optional<Player> getTargetedBy(Player target) {
        return targetMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == target)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private void clearTarget(Player player) {
        targetMap.remove(player);
        ActionBar.clearActionBar(player);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        final Player target = event.getEntity();
        getTargetedBy(target).ifPresent(player -> {
            targetMap.remove(player);
            final Player killer = target.getKiller();

            if (player == killer || deathMessage == null) {
                ActionBar.clearActionBar(player);
                return;
            }

            String message = deathMessage.replace("%target%", target.getName());
            if (killer != null) {
                message = message.replace("%killer%", killer.getName());
            }

            ActionBar.sendActionBar(player, Colors.color(message));
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        Player damaged = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (!isInMinigame(damaged) || !isInMinigame(damager)) return;

        targetMap.put(damager, damaged);
        healthBar.set(damager, healthBarDuration * 1000L);
        showHealthBar(damager, damaged);
    }

}
