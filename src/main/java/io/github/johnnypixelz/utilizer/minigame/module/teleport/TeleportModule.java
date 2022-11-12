package io.github.johnnypixelz.utilizer.minigame.module.teleport;

import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import io.github.johnnypixelz.utilizer.minigame.arena.Arena;
import io.github.johnnypixelz.utilizer.minigame.arena.FFAArena;
import io.github.johnnypixelz.utilizer.minigame.arena.Position;
import io.github.johnnypixelz.utilizer.minigame.arena.TeamedArena;
import io.github.johnnypixelz.utilizer.minigame.module.teams.Team;
import io.github.johnnypixelz.utilizer.minigame.module.teams.TeamsModule;
import io.github.johnnypixelz.utilizer.random.ProbabilityList;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TeleportModule extends MinigameModule {
    private final List<ArenaTeleportHandler> arenaHandlers = new ArrayList<>();
    private boolean teleportToLobby = true;
    private boolean teleportToGame = true;

    public TeleportModule() {
        registerArenaHandlers();
    }

    @Override
    protected void init() {
        getEventManager().getOnMinigameStart().listen(() -> {
            if (!teleportToGame) return;

            teleportPlayersToPositions();
        });

        getEventManager().getOnPlayerJoin().listen(player -> {
            if (!teleportToLobby) return;

            Arena arena = getMinigame().getArena();
            if (!arena.hasLobbyPosition()) return;
            player.teleport(arena.getLobbyPosition().toLocation());
        });
    }

    private void registerArenaHandlers() {
        registerArenaHandler(FFAArena.class, ffaArena -> {
            ProbabilityList<Position> list = new ProbabilityList<>();
            ffaArena.getSpawnPositions().forEach(position -> list.add(position, 1));

            getCurrentPlayers().forEach(player -> player.teleport(list.randomPop().toLocation()));
        });

        registerArenaHandler(TeamedArena.class, teamedArena -> {
            TeamsModule module = getModule(TeamsModule.class);

            for (String name : module.getTeams().keySet()) {
                Team team = module.getTeam(name);

                List<Position> positions = teamedArena.getPositions(name);

                if (team.getPlayerLimit() > positions.size()) {
                    throw new IllegalStateException("Arena has less positions than the team size.");
                }

                List<Player> players = team.getPlayerObjects();
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).teleport(positions.get(i).toLocation());
                }
            }
        });
    }

    public <T extends Arena> void registerArenaHandler(Class<T> arenaClass, Consumer<T> handler) {
        ArenaTeleportHandler arenaHandler = new ArenaTeleportHandler((Class<Arena>) arenaClass, (Consumer<Arena>) handler);
        arenaHandlers.add(arenaHandler);
    }

    public <T extends Arena> ArenaTeleportHandler getHandler(Class<T> arenaClass) {
        for (ArenaTeleportHandler arenaHandler : arenaHandlers) {
            if (arenaHandler == null || !arenaHandler.getClazz().isAssignableFrom(arenaClass)) continue;
            return arenaHandler;
        }

        return null;
    }

    public void teleportPlayersToPositions() {
        Arena arena = getMinigame().getArena();
        ArenaTeleportHandler handler = getHandler(arena.getClass());
        if (handler == null) return;
        handler.getHandler().accept(arena);
    }

    public TeleportModule disableLobbyTeleport() {
        teleportToLobby = false;
        return this;
    }

    public TeleportModule disableDefaultTeleport() {
        teleportToGame = false;
        return this;
    }
}
