package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.common.io.Files;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.ftccommunity.ftcxtensible.collections.ArrayQueue;
import org.ftccommunity.ftcxtensible.robot.Async;
import org.ftccommunity.ftcxtensible.robot.ExtensibleLinearOpMode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.firstinspires.ftc.teamcode.AccelerometerListenerState.START;

/**
 * Created by David on 10/23/2016.
 */
@Autonomous
public class CameraNavigationAuto extends ExtensibleLinearOpMode {
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
        telemetry().data("STATUS", "Initializing");
        VuforiaLocalizer.Parameters vuforiaParams = new VuforiaLocalizer.Parameters(cameraView().getId());
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

        float mmPerInch        = 25.4f;
        float mmBotWidth       = 18 * mmPerInch;            // ... or whatever is right for your robot
        float mmFTCFieldWidth  = (12*12 - 2) * mmPerInch;   // the FTC field is ~11'10" center-to-center of the glass panels

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
                .translation(mmBotWidth / 2,mmBotWidth / 2, mmBotWidth / 2)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.YZY,
                        AngleUnit.DEGREES, -90, 0, 0));

        for (VuforiaTrackable trackable : vuforiaTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(phoneLocationOnRobot, VuforiaLocalizer.CameraDirection.BACK);
        }

        final SensorManager systemService = (SensorManager) appContext().getSystemService(Context.SENSOR_SERVICE);
        final Sensor accelerometer = systemService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        systemService.registerListener(new SensorEventListener() {
            private int accurancy = SensorManager.SENSOR_STATUS_ACCURACY_LOW;
            private ArrayQueue sample = new ArrayQueue(5000);
            private AccelerometerListenerState state = START;
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                    return;

                // // TODO: 10/23/2016  code accerolmeter inpur

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    accurancy = accuracy;
                }
            }
        }, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        waitForStart();

        vuforiaTrackables.activate();
        for (VuforiaTrackable trackable : vuforiaTrackables) {
            VuforiaTrackableDefaultListener listener = (VuforiaTrackableDefaultListener) trackable.getListener();
            telemetry().data(trackable.getName(), listener.isVisible());
            if (listener.getUpdatedRobotLocation() != null)
                lastLocation = listener.getUpdatedRobotLocation();
        }

    }
    

    @Async
    public void sensorLoop() {

    }
}
