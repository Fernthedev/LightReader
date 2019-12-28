package com.github.fernthedev.light.animations;

@FunctionalInterface
public interface AnimationRunnable {

    void run(LedStrip ledStrip);

    default void runAsync(LedStrip ledStrip) {
        new Thread(() -> AnimationRunnable.this.run(ledStrip)).start();
    }

    default void runAsync(LedStrip ledStrip, String name) {
        new Thread(() -> AnimationRunnable.this.run(ledStrip), name).start();
    }



}
