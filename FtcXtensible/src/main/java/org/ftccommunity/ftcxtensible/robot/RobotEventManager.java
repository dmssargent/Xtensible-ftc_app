package org.ftccommunity.ftcxtensible.robot;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RobotEventManager {
    private final static int NUMBER_OF_TASKS = 2;
    private final RobotContext ctx;
    private ExecutorService service = Executors.newFixedThreadPool(NUMBER_OF_TASKS + 1);

    public RobotEventManager(RobotContext ctx) {
        this.ctx = ctx;
    }

    public void run() {
        service.submit(new Runnable() {
            @Override
            public void run() {
                int hash = 0;
                ArrayList<RobotEvent> events = new ArrayList<>();
                while (!Thread.currentThread().isInterrupted()) {
                    final int currHashCode = ctx.events().hashCode();
                    if (currHashCode != hash) {
                        hash = currHashCode;
                        for (RobotEvent event : ctx.events()) {
                            if (!events.contains(event)) {
                                events.add(event);
                            }
                        }

                        for (RobotEvent event : events) {
                            if (!ctx.events().contains(event)) {
                                events.remove(event);
                            }
                        }
                    }

                    try {
                        for (RobotEvent event : events) {
                            if (Thread.currentThread().isInterrupted()) {
                                break;
                            }
                            if (!event.isExecuting()) {
                                service.submit(event);
                            }
                        }
                    } catch (RejectedExecutionException ignored) {

                    }
                }
            }
        });
    }

    public void shutdown() {
        service.shutdown();
        service.shutdownNow();
        try {
            if (!service.awaitTermination(1, TimeUnit.SECONDS)) {
                throw new RuntimeException(new TimeoutException("All events did not terminate in a timely manner"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
