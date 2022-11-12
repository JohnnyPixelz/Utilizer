package io.github.johnnypixelz.utilizer.minigame.module.teams;

import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TeamsModule extends MinigameModule {
    private final Map<String, Team> teams;
    private final boolean friendlyFire;

    public TeamsModule(boolean friendlyFire) {
        this.teams = new HashMap<>();
        this.friendlyFire = friendlyFire;
    }

    public TeamsModule() {
        this.teams = new HashMap<>();
        this.friendlyFire = false;
    }

    @Override
    protected void init() {
        getEventManager().getOnPlayerRemove().listen(player -> {
            for (Team team : teams.values()) {
                team.removePlayer(player);
            }
        });
    }

    public Map<String, Team> getTeams() {
        return teams;
    }

    public Team getTeamWithLeastPlayers() {
        if (teams.size() == 0) {
            throw new IllegalStateException("Attempted to get team with least amount of players but no teams exist.");
        }

        return Collections.min(teams.values(), Comparator.comparingInt(o -> o.getPlayers().size()));
    }

    public Team getTeam(String name) {
        return teams.get(name);
    }

    public Team getTeam(Player player) {
        for (Team team : teams.values()) {
            if (team.containsPlayer(player)) {
                return team;
            }
        }

        return null;
    }

    public Team createTeam(String name) {
        if (teams.containsKey(name)) return null;

        Team team = new Team(name);
        teams.put(name, team);
        return team;
    }

    public void removeTeam(Team team) {
        teams.remove(team.getId());
    }

    @EventHandler
    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (friendlyFire) return;

        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        if (!isInMinigame(damaged) || !isInMinigame(damaged)) return;

        Team team = getTeam(damager);
        if (team == null) return;

        if (team.areTeammates(damager, damaged)) {
            event.setCancelled(true);
        }
    }

}
