package io.github.johnnypixelz.utilizer.minigame.module;

import io.github.johnnypixelz.utilizer.tasks.Tasks;
import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.event.StatelessEventEmitter;
import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class WarmUpModule extends MinigameModule {
    private final int warmUpPlayerThreshold;
    private final int warmUpDuration;
    private BukkitTask warmUpTask;
    private final StatefulEventEmitter<Integer> onWarmUpStart;
    private final StatefulEventEmitter<Integer> onWarmUpTick;
    private final StatelessEventEmitter onWarmUpInterrupt;

    public WarmUpModule(int warmUpPlayerThreshold, int warmUpDuration) {
        this.warmUpPlayerThreshold = warmUpPlayerThreshold;
        this.warmUpDuration = warmUpDuration;
        this.onWarmUpStart = new StatefulEventEmitter<>();
        this.onWarmUpTick = new StatefulEventEmitter<>();
        this.onWarmUpInterrupt = new StatelessEventEmitter();
    }

    @Override
    protected void init() {
        getEventManager().getOnPlayerJoin().listen(player -> {
            if (warmUpTask != null || getCurrentPlayerAmount() < warmUpPlayerThreshold) return;
            startWarmUp();
        }, EventPriority.HIGHEST);
    }

    private void startWarmUp() {
        if (warmUpTask != null) return;

        onWarmUpStart.emit(warmUpDuration);

        AtomicInteger timeRemaining = new AtomicInteger(warmUpDuration);
        warmUpTask = Tasks.sync().timed(() -> {
            if (getCurrentPlayerAmount() < warmUpPlayerThreshold) {
                warmUpTask.cancel();
                warmUpTask = null;
                onWarmUpInterrupt.emit();
                return;
            }

            if (timeRemaining.get() == 0) {
                getMinigame().start();
                warmUpTask.cancel();
                warmUpTask = null;
                return;
            }

            onWarmUpTick.emit(timeRemaining.get());
            timeRemaining.set(timeRemaining.get() - 1);
        }, 20, warmUpDuration + 1);
    }

    public WarmUpModule onWarmUpStart(Consumer<Integer> onWarmUpStart) {
        this.onWarmUpStart.listen(onWarmUpStart);
        return this;
    }

    public WarmUpModule onWarmUpTick(Consumer<Integer> onWarmUpTick) {
        this.onWarmUpTick.listen(onWarmUpTick);
        return this;
    }

    public WarmUpModule onWarmUpInterrupt(Runnable onWarmUpInterrupt) {
        this.onWarmUpInterrupt.listen(onWarmUpInterrupt);
        return this;
    }

    public WarmUpModule enableDefaultWarmUpInterruptMessage() {
        this.onWarmUpInterrupt.listen(() -> broadcast("Warm up cancelled due to not enough players."));
        return this;
    }

    public WarmUpModule enableDefaultWarmUpTickMessage() {
        this.onWarmUpTick.listen(integer -> broadcast("Starting in " + integer));
        return this;
    }

    public WarmUpModule enableDefaultWarmUpTickMessage(int threshold) {
        this.onWarmUpTick.listen(integer -> {
            if (integer > threshold) return;
            broadcast("Starting in " + integer);
        });
        return this;
    }

    public WarmUpModule enableDefaultWarmUpStartMessage() {
        this.onWarmUpStart.listen(integer -> broadcast("Starting in " + integer));
        return this;
    }

}
