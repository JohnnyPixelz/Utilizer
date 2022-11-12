package io.github.johnnypixelz.utilizer.minigame.module;

import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.event.StatelessEventEmitter;
import io.github.johnnypixelz.utilizer.minigame.MinigameModule;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@Deprecated
public class EventModule extends MinigameModule {
    private final StatefulEventEmitter<Player> onPreJoin;
    private final StatefulEventEmitter<Player> onPostJoin;
    private final StatefulEventEmitter<Player> onRemove;
    private final StatefulEventEmitter<Player> onQuit;
    private final StatelessEventEmitter onStart;
    private final StatelessEventEmitter onFinish;
    private final StatelessEventEmitter onCleanup;

    public EventModule() {
        onPreJoin = new StatefulEventEmitter<>();
        onPostJoin = new StatefulEventEmitter<>();
        onRemove = new StatefulEventEmitter<>();
        onQuit = new StatefulEventEmitter<>();
        onStart = new StatelessEventEmitter();
        onFinish = new StatelessEventEmitter();
        onCleanup = new StatelessEventEmitter();
    }

    public EventModule setOnPreJoin(Consumer<Player> onPreJoin) {
        this.onPreJoin.listen(onPreJoin);
        return this;
    }

    public EventModule setOnPostJoin(Consumer<Player> onPostJoin) {
        this.onPostJoin.listen(onPostJoin);
        return this;
    }

    public EventModule setOnRemove(Consumer<Player> onRemove) {
        this.onRemove.listen(onRemove);
        return this;
    }

    public EventModule setOnQuit(Consumer<Player> onQuit) {
        this.onQuit.listen(onQuit);
        return this;
    }

    public EventModule setOnStart(Runnable onStart) {
        this.onStart.listen(onStart);
        return this;
    }

    public EventModule setOnFinish(Runnable onFinish) {
        this.onFinish.listen(onFinish);
        return this;
    }

    public EventModule setOnCleanup(Runnable onCleanup) {
        this.onCleanup.listen(onCleanup);
        return this;
    }

}
