package org.firstinspires.ftc.teamcode.examples;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.common.collect.Queues;
import com.google.common.io.Files;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.RollingAverage;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.ftccommunity.bindings.DataBinder;
import org.ftccommunity.ftcxtensible.collections.TripleAxis;
import org.ftccommunity.ftcxtensible.robot.Async;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Queue;

import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.METER;

/**
 * Created by David on 10/23/2016.
 */
@Autonomous
@Disabled
public class CameraNavigationAuto extends LinearOpMode {
    private OpenGLMatrix lastLocation;
    private int VUF_WHEEL_TARGET = 0;
    private int VUF_TOOL_TARGET = 1;
    private int VUF_LEGOS_TARGET = 2;
    private int VUF_GEARS_TARGET = 3;

    /**
     * Override this method and place your code here.
     * <p>
     * Please do not swallow the InterruptedException, as it is used in cases
     * where the op mode needs to be terminated early.
     *
     * @throws InterruptedException
     */
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("STATUS", "Initializing");
        telemetry.update();

        int id = DataBinder.getInstance().integers().get(DataBinder.CAMERA_VIEW);
        VuforiaLocalizer.Parameters vuforiaParams = new VuforiaLocalizer.Parameters(id);
        try {
            vuforiaParams.vuforiaLicenseKey = Files.toString(new File("/sdcard/robot/vuforia.key"), Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        vuforiaParams.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(vuforiaParams);
        VuforiaTrackables vuforiaTrackables = vuforia.loadTrackablesFromAsset("FTC_2016-17");

        // Set names of trackables
        VUF_WHEEL_TARGET = 0;
        vuforiaTrackables.get(VUF_WHEEL_TARGET).setName("Wheels");
        vuforiaTrackables.get(VUF_TOOL_TARGET).setName("Tools");


        VUF_LEGOS_TARGET = 2;
        vuforiaTrackables.get(VUF_LEGOS_TARGET).setName("Legos");

        VUF_GEARS_TARGET = 3;
        vuforiaTrackables.get(VUF_GEARS_TARGET).setName("Gears");


        // Location on the field
        /*
        * Blue Audience Wall  |
        *                     |
        *              Wheels |
        *                     |
        *               Legos |
        *                     |
        *                     |_____________
        *                        |    |
        *                    Tools    Gears
        *
        *                    Red Audience Wall
        *
        *  Each "_" is equal to 12", whereas each "|" is equal to 24." However the edges are equal to
        *  their respective figures minus 1" (edge "_" = 11"; edge "|' = 23")
        */

        float mmPerInch = 25.4f;
        float mmBotWidth = 18 * mmPerInch;            // ... or whatever is right for your robot
        float mmFTCFieldWidth = (12 * 12 - 2) * mmPerInch;   // the FTC field is ~11'10" center-to-center of the glass panels

        final OpenGLMatrix gearsFieldLocation = OpenGLMatrix
                /* Then we translate the target off to the RED WALL. Our translation here
                is a negative translation in X.*/
                .translation(-mmFTCFieldWidth / 2, -12 * mmPerInch, 0)
                .multiplied(Orientation.getRotationMatrix(
                        /* First, in the fixed (field) coordinate system, we rotate 90deg in X, then 90 in Z */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        vuforiaTrackables.get(3).setLocation(gearsFieldLocation);
        final OpenGLMatrix toolsFieldLocation = OpenGLMatrix
                /* Then we translate the target off to the RED WALL. Our translation here
                is a negative translation in X.*/
                .translation(-mmFTCFieldWidth / 2, (24 + 12) * mmPerInch, 0)
                .multiplied(Orientation.getRotationMatrix(
                        /* First, in the fixed (field) coordinate system, we rotate 90deg in X, then 90 in Z */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        vuforiaTrackables.get(1).setLocation(toolsFieldLocation);
        final OpenGLMatrix wheelsLocationOnField = OpenGLMatrix
                /* Then we translate the target off to the Blue Audience wall.
                Our translation here is a positive translation in Y.*/
                .translation(12 * mmPerInch, mmFTCFieldWidth / 2, 0)
                .multiplied(Orientation.getRotationMatrix(
                        /* First, in the fixed (field) coordinate system, we rotate 90deg in X */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 0, 0));
        vuforiaTrackables.get(0).setLocation(wheelsLocationOnField);
        final OpenGLMatrix legosLocationOnField = OpenGLMatrix
                /* Then we translate the target off to the Blue Audience wall.
                Our translation here is a positive translation in Y.*/
                .translation((-24 - 12) * mmPerInch, mmFTCFieldWidth / 2, 0)
                .multiplied(Orientation.getRotationMatrix(
                        /* First, in the fixed (field) coordinate system, we rotate 90deg in X */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 0, 0));
        vuforiaTrackables.get(2).setLocation(legosLocationOnField);

        // Assume the phone is at the center of the robot
        final OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
                .translation(mmBotWidth / 2, mmBotWidth / 2, mmBotWidth / 2)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.YZY,
                        AngleUnit.DEGREES, -90, 0, 0));

        for (VuforiaTrackable trackable : vuforiaTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(phoneLocationOnRobot, VuforiaLocalizer.CameraDirection.BACK);
        }

        idle();
        final SensorManager systemService = (SensorManager) hardwareMap.appContext.getSystemService(Context.SENSOR_SERVICE);
        final Sensor accelerometer = systemService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        AccelerometerListener listener1 = new AccelerometerListener();
        systemService.registerListener(listener1, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Thread aDataLoop = new Thread(new Runnable() {
            @Override
            public void run() {
                listener1.processData();
            }
        });
        aDataLoop.start();
        try {
            telemetry.addData("STATUS", "Ready");
            telemetry.update();
            waitForStart();
            telemetry.addData("STATUS", "Running");
        } catch (RuntimeException ex) {
            // ignore bug
        }

        vuforiaTrackables.activate();
        while (opModeIsActive()) {
            for (VuforiaTrackable trackable : vuforiaTrackables) {
                VuforiaTrackableDefaultListener listener = (VuforiaTrackableDefaultListener) trackable.getListener();
                telemetry.addData(trackable.getName(), listener.isVisible());
                OpenGLMatrix updatedRobotLocation = listener.getUpdatedRobotLocation();
                if (updatedRobotLocation != null)
                    lastLocation = updatedRobotLocation;

            }
            if (lastLocation != null)
                telemetry.addData("I_LOCATION", lastLocation.formatAsTransform());
            Position currentPosition = listener1.getCurrentPosition();
            telemetry.addData("A_LOCATION", "X: " + currentPosition.x + " Y: " + currentPosition.y + " Z: " + currentPosition.z);
            telemetry.addData("ACCEL_DATA", listener1.getDataDisplay());
            telemetry.addData("ACCEL_DATA_READY", listener1.isInitialized());
            telemetry.update();
        }
        aDataLoop.interrupt();
        vuforiaTrackables.deactivate();


    }


    @Async
    public void sensorLoop() {

    }

    private static class AccelerometerListener implements SensorEventListener {
        //private AccelerometerListenerState state = START;
        private final Queue<SensorEvent> dataQueue = Queues.synchronizedDeque(Queues.<SensorEvent>newArrayDeque());
        private BufferedWriter bufferedWriter;
        private OutputStream fileStream;
        private int accurancy = SensorManager.SENSOR_STATUS_ACCURACY_LOW;
        private Position currentPosition = new Position(METER, 0, 0, 0, System.nanoTime());
        private Velocity currentVelocity = new Velocity(METER, 0, 0, 0, System.nanoTime());
        private long lastTimestamp = 0;
        private String currentData;
        private boolean initialized = false;

        public AccelerometerListener() {

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
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accurancy = accuracy;
            }
        }

        public void processData() {
            final int SAMPLE_SIZE = 5;
            TripleAxis<RollingAverage> averages = new TripleAxis<>(new RollingAverage(3000), new RollingAverage(3000), new RollingAverage(3000));
            //boolean initialized = false;
            double tar[] = new double[3];
            while (!Thread.currentThread().isInterrupted()) {
                if (dataQueue.isEmpty()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    continue;
                }

                SensorEvent poll = dataQueue.poll();
                if (lastTimestamp == 0) lastTimestamp = poll.timestamp;
                double dt = (poll.timestamp - lastTimestamp) / 1E9;
                // Now set timestamp
                lastTimestamp = poll.timestamp;
                averages.X.addNumber((int) (poll.values[0] * 100 - tar[0]));
                double x = Math.round(averages.X.getAverage() / 10) / 10d;
                averages.Y.addNumber((int) (poll.values[1] * 100 - tar[1]));
                double y = Math.round(averages.Y.getAverage() / 10) / 10d;
                averages.Z.addNumber((int) (poll.values[2] * 100 - tar[2]));
                double z = Math.round(averages.Z.getAverage() / 10) / 10d;
                RobotLog.d("%s,%s,%s,%s", x, y, z, dt);

                if (!initialized) {
                    if (averages.X.size() == 3000) {
                        initialized = true;
                        tar[0] = averages.X.getAverage();
                        tar[1] = averages.Y.getAverage();
                        tar[2] = averages.Z.getAverage();

                        averages = new TripleAxis<>(new RollingAverage(SAMPLE_SIZE), new RollingAverage(SAMPLE_SIZE), new RollingAverage(SAMPLE_SIZE));
                    }

                    continue;
                }

                try {
                    currentData = String.format("%s,%s,%s,%s\n", x, y, z, dt);
                    bufferedWriter.write(currentData);
                    bufferedWriter.flush();
                } catch (IOException e) {
                    RobotLog.w(e.toString());
                }
                currentVelocity.acquisitionTime = poll.timestamp;
                currentVelocity.xVeloc = computeVelocity(x, dt, currentVelocity.xVeloc);
                currentVelocity.yVeloc = computeVelocity(y, dt, currentVelocity.yVeloc);
                currentVelocity.zVeloc = computeVelocity(z, dt, currentVelocity.zVeloc);
                currentPosition.acquisitionTime = poll.timestamp;
                currentPosition.x = computePosition(x, dt, currentVelocity.xVeloc, currentPosition.x);
                currentPosition.y = computePosition(y, dt, currentVelocity.yVeloc, currentPosition.y);
                currentPosition.z = computePosition(z, dt, currentVelocity.zVeloc, currentPosition.z);
            }
        }

        private double computePosition(double a, double dt, double velocity, double lastPosition) {
            return .5 * a * dt * dt + dt * velocity + lastPosition;
        }

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
    }
}
