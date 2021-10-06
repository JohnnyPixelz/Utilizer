package io.github.johnnypixelz.utilizer;

import java.util.function.Consumer;

public class Benchmark {

    public static void run(Runnable runnable, Consumer<Long> milliseconds) {
        long ms = System.currentTimeMillis();
        runnable.run();
        ms = System.currentTimeMillis() - ms;
        milliseconds.accept(ms);
    }

    public static long run(Runnable runnable) {
        long ms = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - ms;
    }

}
