package com.github.fernthedev.light.animations;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@FunctionalInterface
public interface AnimationRunnable {

    void run(LedStrip ledStrip);

    default void runAsync(LedStrip ledStrip) {
        new Thread(() -> AnimationRunnable.this.run(ledStrip)).start();
    }

    default void runAsync(LedStrip ledStrip, String name) {
        new Thread(() -> AnimationRunnable.this.run(ledStrip), name).start();
    }

    default Future<?> runAsync(LedStrip ledStrip, ExecutorService service) {
        return CompletableFuture.runAsync(() -> AnimationRunnable.this.run(ledStrip), service);
    }


}
