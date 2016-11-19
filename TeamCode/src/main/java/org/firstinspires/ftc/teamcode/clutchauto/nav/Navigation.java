package org.firstinspires.ftc.teamcode.clutchauto.nav;

import android.content.Context;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Navigation {
    private final NavigationAccel navigationAccel;
    private final NavigationCamera navigationCamera;
    private final UpdateLoop loop;
    private ExecutorService singleThread = Executors.newSingleThreadExecutor();

    public Navigation(Context appContext) {
        navigationCamera = NavigationCamera.getInstance();
        navigationAccel = NavigationAccel.getInstance(appContext);
        navigationCamera.activate();
        navigationAccel.initialize();
        loop = new UpdateLoop();
        loop.add(navigationCamera.loop()).add(navigationAccel.loop());
        singleThread.submit(loop);
    }

    public Position position() {
        if (navigationCamera.hasUsableData())
                return navigationCamera.currentLocation();

        return navigationAccel.position();
    }

    public Velocity velocity() {
        navigationCamera.lock();
        try {
            if (navigationCamera.hasUsableData()) {
                return navigationCamera.velocity();
            }
        } finally {
            navigationCamera.unlock();
        }

        return navigationAccel.velocity();
    }


    public void close() {
        navigationAccel.close();
        navigationCamera.deactivate();
        loop.clear();
        singleThread.shutdown();
    }

    private static class UpdateLoop implements Runnable {
        private final LinkedList<Runnable> runnables = new LinkedList<>();
        private boolean isRunning = true;

        UpdateLoop add(@NotNull Runnable runnable) {
            runnables.add(runnable);
            return this;
        }

        void clear() {
            runnables.clear();
        }

        @Override
        public void run() {
            while (isRunning && !Thread.currentThread().isInterrupted()) {
                for (Runnable runnable : runnables) {
                    runnable.run();
                }
                Thread.yield();
            }
        }
    }
}
