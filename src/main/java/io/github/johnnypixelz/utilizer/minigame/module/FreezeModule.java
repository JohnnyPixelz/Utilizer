package io.github.johnnypixelz.utilizer.minigame.module;

import io.github.johnnypixelz.utilizer.tasks.Tasks;
import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.event.StatelessEventEmitter;
import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FreezeModule extends MinigameModule {
    private boolean active = false;
    private final int duration;
    private boolean defaultStart = true;
    private final StatefulEventEmitter<Integer> onFreezeTick;
    private final StatelessEventEmitter onFreezeEnd;
    private Predicate<Player> bypassFreeze;
    private BukkitTask countdownTask;
    private BukkitTask finishTask;

    public FreezeModule() {
        this(10);
    }

    public FreezeModule(int duration) {
        this.duration = duration;
        this.active = false;
        this.onFreezeTick = new StatefulEventEmitter<>();
        this.onFreezeEnd = new StatelessEventEmitter();
    }

    @Override
    protected void init() {
        getEventManager().getOnMinigameStart().listen(() -> {
            if (!defaultStart) return;
            start();
        }, EventPriority.HIGH);
        getEventManager().getOnMinigameFinish().listen(this::stop);
    }

    public FreezeModule bypassFreeze(Predicate<Player> bypassCondition) {
        bypassFreeze = bypassCondition;
        return this;
    }

    public FreezeModule onFreezeTick(Consumer<Integer> freezeTick) {
        this.onFreezeTick.listen(freezeTick);
        return this;
    }

    public FreezeModule onFreezeEnd(Runnable freezeEnd) {
        this.onFreezeEnd.listen(freezeEnd);
        return this;
    }

    public void start() {
        if (active) return;
        setActive(true);

        AtomicInteger timeLeft = new AtomicInteger(duration);
        countdownTask = Tasks.sync().timed(() -> {
            onFreezeTick.emit(timeLeft.getAndDecrement());
        }, 20, duration);

        finishTask = Tasks.sync().delayed(() -> {
            setActive(false);
            onFreezeEnd.emit();
        }, duration * 20L);
    }

    public void stop() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }

        if (finishTask != null) {
            finishTask.cancel();
        }
    }

    private void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public FreezeModule disableDefaultStart() {
        defaultStart = false;
        return this;
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (!active) return;

        Player player = event.getPlayer();
        if (!isInMinigame(player)) return;

        if (bypassFreeze != null && bypassFreeze.test(player)) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.distanceSquared(to) == 0) return;
        player.teleport(from);
    }
}
