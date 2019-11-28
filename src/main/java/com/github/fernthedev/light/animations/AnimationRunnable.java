package com.github.fernthedev.light.animations;

public abstract class AnimationRunnable implements Runnable {

    public void runAsync() {
        new Thread(this).start();
    }

    public void runAsync(String name) {
        new Thread(this, name).start();
    }

}
