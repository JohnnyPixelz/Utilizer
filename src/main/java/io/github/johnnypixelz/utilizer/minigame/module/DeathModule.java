package io.github.johnnypixelz.utilizer.minigame.module;

import io.github.johnnypixelz.utilizer.Scheduler;
import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.exp.Exp;
import io.github.johnnypixelz.utilizer.itemstack.Items;
import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import io.github.johnnypixelz.utilizer.minigame.arena.Arena;
import io.github.johnnypixelz.utilizer.minigame.arena.FFAArena;
import io.github.johnnypixelz.utilizer.minigame.arena.TeamedArena;
import io.github.johnnypixelz.utilizer.minigame.module.teams.Team;
import io.github.johnnypixelz.utilizer.minigame.module.teams.TeamsModule;
import io.github.johnnypixelz.utilizer.serialize.world.Position;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DeathModule extends MinigameModule {
    private final Map<Class<Arena>, BiFunction<Arena, Player, Position>> respawnPositionHandlers;
    private final Map<UUID, Position> respawnPositions;
    private int respawnDelay;
    private final StatefulEventEmitter<Player> onRespawn;
    private Predicate<Player> respawnPredicate;

    public DeathModule() {
        this(-1);
    }

    public DeathModule(int respawnDelayForSpectators) {
        this.respawnPositionHandlers = new HashMap<>();
        this.respawnPositions = new HashMap<>();
        this.respawnDelay = respawnDelayForSpectators;
        this.onRespawn = new StatefulEventEmitter<>();
        this.respawnPredicate = null;

        registerRespawnPositionHandler(FFAArena.class, (ffaArena, event) -> {
            return ffaArena.getRandomSpawnPosition();
        });

        registerRespawnPositionHandler(TeamedArena.class, (teamedArena, event) -> {
            Player player = event.getPlayer();
            TeamsModule module = getModule(TeamsModule.class);
            Team team = module.getTeam(player);

            List<Position> positions = teamedArena.getPositions(team.getId());
            return positions.get(ThreadLocalRandom.current().nextInt(positions.size()));
        });
    }

    public DeathModule setRespawnPredicate(Predicate<Player> respawnPredicate) {
        this.respawnPredicate = respawnPredicate;
        return this;
    }

    public int getRespawnDelay() {
        return respawnDelay;
    }

    public void setRespawnDelay(int delay) {
        this.respawnDelay = delay;
    }

    public void setRespawnPosition(UUID uuid, Position position) {
        respawnPositions.put(uuid, position);
    }

    public void setRespawnPosition(Player player, Position position) {
        setRespawnPosition(player.getUniqueId(), position);
    }

    public DeathModule onRespawn(Consumer<Player> onRespawn) {
        this.onRespawn.listen(onRespawn);
        return this;
    }

    private <T extends Arena> void registerRespawnPositionHandler(
            Class<T> clazz,
            BiFunction<T, Player, Position> handler
    ) {
        respawnPositionHandlers.put((Class<Arena>) clazz, (BiFunction<Arena, Player, Position>) handler);
    }

    public void respawn(Player player) {
        if (respawnPredicate != null) {
            boolean test = respawnPredicate.test(player);
            if (!test) return;
        }

        SpectateModule spectateModule = getModule(SpectateModule.class);
        if (!spectateModule.isSpectator(player)) return;

        PlayerRespawnEvent event = new PlayerRespawnEvent(player, player.getWorld().getSpawnLocation(), false);
        Bukkit.getPluginManager().callEvent(event);
        player.teleport(event.getRespawnLocation());
    }

    private Location getRespawnLocation(Player player, Arena arena) {
        Position position = respawnPositions.get(player.getUniqueId());
        if (position != null) {
            return position.toLocation();
        }

        for (Class<Arena> arenaClass : respawnPositionHandlers.keySet()) {
            if (!arenaClass.isAssignableFrom(arena.getClass())) continue;
            return respawnPositionHandlers.get(arenaClass).apply(arena, player).toLocation();
        }

        return player.getWorld().getSpawnLocation();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onFatalDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (!isInMinigame(player)) return;
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        SpectateModule spectateModule = getModule(SpectateModule.class);
        if (spectateModule == null) return;

        // Player should die from incoming damage
        event.setCancelled(true);
        Location deathLocation = player.getLocation().clone();

        List<ItemStack> drops = new ArrayList<>();
        for (ItemStack content : player.getInventory().getContents()) {
            if (Items.isNull(content)) continue;
            drops.add(content);
        }
        for (ItemStack armorContent : player.getInventory().getArmorContents()) {
            if (Items.isNull(armorContent)) continue;
            drops.add(armorContent);
        }

        player.setLastDamageCause(event);
        PlayerDeathEvent playerDeathEvent =
                new PlayerDeathEvent(
                        player,
                        drops,
                        player.getTotalExperience(),
                        ""
                );
        Bukkit.getPluginManager().callEvent(playerDeathEvent);

        spectateModule.enterSpectatorMode(player);
        player.setVelocity(new Vector(0, 1, 0));
        player.setHealth(player.getMaxHealth());

        if (!playerDeathEvent.getKeepInventory()) {
            playerDeathEvent.getDrops()
                    .forEach(drop -> deathLocation.getWorld().dropItemNaturally(deathLocation, drop));
            player.getInventory().clear();
        }

        if (!playerDeathEvent.getKeepLevel()) {
            Exp.setTotalExperience(player, 0);
        }

        int droppedExp = playerDeathEvent.getDroppedExp();
        if (droppedExp > 0) {
            ExperienceOrb orb = deathLocation.getWorld().spawn(deathLocation, ExperienceOrb.class);
            orb.setExperience(droppedExp);
        }

        if (respawnDelay > 0) {
            Scheduler.syncDelayed(() -> respawn(player), respawnDelay * 20L);
        }
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        Arena arena = getMinigame().getArena();
        Location respawnLocation = getRespawnLocation(player, arena);
        event.setRespawnLocation(respawnLocation);

        // Handle spectators
        SpectateModule spectateModule = getModule(SpectateModule.class);
        if (spectateModule != null) {
            spectateModule.exitSpectatorMode(event.getPlayer());
        }

        onRespawn.emit(player);
    }
}
