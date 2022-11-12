package io.github.johnnypixelz.utilizer.minigame.module.teleport;

import io.github.johnnypixelz.utilizer.minigame.arena.Arena;

import java.util.function.Consumer;

public class ArenaTeleportHandler {
    private final Class<Arena> clazz;
    private final Consumer<Arena> handler;

    public ArenaTeleportHandler(Class<Arena> clazz, Consumer<Arena> handler) {
        this.clazz = clazz;
        this.handler = handler;
    }

    public Class<Arena> getClazz() {
        return clazz;
    }

    public Consumer<Arena> getHandler() {
        return handler;
    }
}
