package org.firstinspires.ftc.teamcode.clutchauto.nav;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.Queues;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.RollingAverage;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.TripleAxis;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.METER;

/**
 * Created by David on 11/2/2016.
 */
class NavigationAccel {
    private static NavigationAccel instance = null;
    private final AccelerometerListener accelerometerListener;
    private final SensorManager systemService;
    private final Sensor accelerometer;
    private final NavigationRunnable navigationInterface;

    private Thread dataProcessingLoop;

    private NavigationAccel(@NotNull Context appContext) {
        checkState(instance == null, "Already initialized");

        systemService = (SensorManager) appContext.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = systemService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometerListener = new AccelerometerListener();
        navigationInterface = new NavigationRunnable(accelerometerListener);

        initialize();
    }

    @NotNull
    public static NavigationAccel getInstance(@NotNull Context appContext) {
        checkNotNull(appContext, "appContext is null");
        if (instance == null) instance = new NavigationAccel(appContext);
        return instance;
    }

    void initialize() {
        systemService.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        dataProcessingLoop = new Thread(accelerometerListener);
        dataProcessingLoop.setName("Data Processing Loop");
        dataProcessingLoop.start();
    }

    void close() {
        dataProcessingLoop.interrupt();
        systemService.unregisterListener(accelerometerListener);
    }

    @NotNull
    Position position() {
        checkState(isReady(), "Data is not ready");
        return accelerometerListener.currentPosition;
    }

    @NotNull
    Velocity velocity() {
        checkState(isReady(), "Data is not ready");
        return accelerometerListener.currentVelocity;
    }

    boolean isReady() {
        return accelerometerListener.initialized;
    }

    public Runnable loop() {
        return accelerometerListener;
    }

    private static class AccelerometerListener implements SensorEventListener, Runnable {
        private BufferedWriter bufferedWriter;
        private Position currentPosition = new Position(METER, 0,0,0, System.nanoTime());
        private Velocity currentVelocity = new Velocity(METER, 0, 0, 0, System.nanoTime());
        private long lastTimestamp = 0;
        //private AccelerometerListenerState state = START;
        private final Queue<SensorEvent> dataQueue = Queues.synchronizedDeque(Queues.<SensorEvent>newArrayDeque());
        private String currentData;
        private boolean initialized = false;

        AccelerometerListener() {
            File file = new File("/sdcard/robot/gryo-data.csv");

            try {
                bufferedWriter = new BufferedWriter(new FileWriter(file));
            } catch (IOException e) {
                RobotLog.e(e.getMessage());
            }
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;

            dataQueue.add(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        void processData() {
            final int SAMPLE_SIZE = 5;
            TripleAxis<RollingAverage> averages = new TripleAxis<>(new RollingAverage(3000), new RollingAverage(3000), new RollingAverage(3000));
            //boolean initialized = false;
            double tar[] = new double[3];
            while (!Thread.currentThread().isInterrupted()) {
                averages = updateDataSingle(SAMPLE_SIZE, averages, tar);
                if (averages == null) return;
            }
        }

        @Nullable
        private TripleAxis<RollingAverage> updateDataSingle(int SAMPLE_SIZE, TripleAxis<RollingAverage> averages, double[] tar) {
            try {
                averages = getRollingAverageTripleAxis(SAMPLE_SIZE, averages, tar);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
            return averages;
        }

        @NonNull
        private TripleAxis<RollingAverage> getRollingAverageTripleAxis(int SAMPLE_SIZE, TripleAxis<RollingAverage> averages, double[] tar) throws InterruptedException {
            if (dataQueue.isEmpty())
                Thread.sleep(10);

            SensorEvent poll = dataQueue.poll();
            if (lastTimestamp == 0)
                lastTimestamp = poll.timestamp;

            double dt = (poll.timestamp - lastTimestamp) / 1E9;
            // Now set timestamp
            addDataToAverage(averages, tar, poll);

            double x = Math.round(averages.X.getAverage() / 10) / 10d;
            double y = Math.round(averages.Y.getAverage() / 10) / 10d;
            double z  = Math.round(averages.Z.getAverage() / 10) / 10d;
            RobotLog.d("%s,%s,%s,%s", x, y, z, dt);

            if (!initialized) {
                if (averages.X.size() == 3000)
                    averages = initializeFromAverage(SAMPLE_SIZE, averages, tar);

                return averages;
            }

            writeData(dt, x, y, z);
            computeVelocity(poll, dt, x, y, z);
            computePosition(poll, dt, x, y, z);

            return averages;
        }

        private void addDataToAverage(TripleAxis<RollingAverage> averages, double[] tar, SensorEvent poll) {
            lastTimestamp = poll.timestamp;
            averages.X.addNumber((int) (poll.values[0] * 100 - tar[0]));
            averages.Y.addNumber((int) (poll.values[1] * 100 - tar[1]));
            averages.Z.addNumber((int) (poll.values[2] * 100 - tar[2]));
        }

        @NonNull
        private TripleAxis<RollingAverage> initializeFromAverage(int SAMPLE_SIZE, TripleAxis<RollingAverage> averages, double[] tar) {
            initialized = true;
            tar[0] = averages.X.getAverage();
            tar[1] = averages.Y.getAverage();
            tar[2] = averages.Z.getAverage();

            averages = new TripleAxis<>(new RollingAverage(SAMPLE_SIZE), new RollingAverage(SAMPLE_SIZE), new RollingAverage(SAMPLE_SIZE));
            return averages;
        }

        private void writeData(double dt, double x, double y, double z) {
            try {
                currentData = String.format("%s,%s,%s,%s\n", x, y, z, dt);
                bufferedWriter.write(currentData);
                bufferedWriter.flush();
            } catch (IOException e) {
                RobotLog.w(e.toString());
            }
        }

        private void computePosition(SensorEvent poll, double dt, double x, double y, double z) {
            currentPosition.acquisitionTime = poll.timestamp;
            currentPosition.x = computePosition(x, dt, currentVelocity.xVeloc, currentPosition.x);
            currentPosition.y = computePosition(y, dt, currentVelocity.yVeloc, currentPosition.y);
            currentPosition.z = computePosition(z, dt, currentVelocity.zVeloc, currentPosition.z);
        }

        @Contract(pure = true)
        private double computePosition(double a, double dt, double velocity, double lastPosition) {
            return .5 * a * dt * dt + dt * velocity + lastPosition;
        }

        private void computeVelocity(SensorEvent poll, double dt, double x, double y, double z) {
            currentVelocity.acquisitionTime = poll.timestamp;
            currentVelocity.xVeloc = computeVelocity(x, dt, currentVelocity.xVeloc);
            currentVelocity.yVeloc = computeVelocity(y, dt, currentVelocity.yVeloc);
            currentVelocity.zVeloc = computeVelocity(z, dt, currentVelocity.zVeloc);
        }

        @Contract(pure = true)
        private double computeVelocity(double a, double dt, double initialVelocity) {
            return .5 * a * dt + initialVelocity;
        }

        public Position getCurrentPosition() {
            return currentPosition;
        }

        String getDataDisplay() {
            return currentData;
        }

        boolean isInitialized() {
            return initialized;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            try {
                processData();
            } catch (Exception ex) {
                RobotLog.e(ex.toString());
            } finally {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    RobotLog.e(e.toString());
                }
            }
        }
    }

    private static class NavigationRunnable implements Runnable {
        final int SAMPLE_SIZE = 5;
        TripleAxis<RollingAverage> averages = new TripleAxis<>(new RollingAverage(3000), new RollingAverage(3000), new RollingAverage(3000));
        //boolean initialized = false;
        final double tar[] = new double[3];
        final AccelerometerListener listener;

        private NavigationRunnable(AccelerometerListener listener) {
            this.listener = listener;
        }

        public void run() {
            averages = listener.updateDataSingle(SAMPLE_SIZE, averages, tar);
        }
    }
}
