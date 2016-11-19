package org.firstinspires.ftc.teamcode.clutchauto.nav;

import android.support.annotation.NonNull;

import com.google.common.io.Files;

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
import org.ftccommunity.ftcxtensible.core.exceptions.RuntimeIOException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;

import static com.google.common.base.Preconditions.checkState;
import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.MM;

/**
 * Created by David on 11/2/2016.
 */

class NavigationCamera {
    private final static NavigationCamera instance = new NavigationCamera();
    private static final int VUF_WHEEL_TARGET = 0;
    private static final int VUF_TOOL_TARGET = 1;
    private static final int VUF_LEGOS_TARGET = 2;
    private static final int VUF_GEARS_TARGET = 3;
    private final double mmPerInch = 25.4;
    private final double mmBotWidth = 18 * mmPerInch ;
    private final double mmFTCFieldWidth = (12 * 12 - 2) * mmPerInch;
    private final CameraWatcher watcher;
    private final VuforiaTrackables vuforiaTrackables;

    private NavigationCamera() throws RuntimeIOException {
        checkState(instance == null, "Already initialized");
        VuforiaLocalizer vuforia = obtainVuforia();
        vuforiaTrackables = obtainVuforiaTrackableObjects(vuforia);
        positionTargets(vuforiaTrackables);
        watcher = CameraWatcher.getInstance(vuforiaTrackables);
    }

    public static NavigationCamera getInstance() {
        return instance;
    }

    @NonNull
    private VuforiaLocalizer obtainVuforia() {
        int id = DataBinder.getInstance().integers().get(DataBinder.CAMERA_VIEW);
        VuforiaLocalizer.Parameters vuforiaParams = new VuforiaLocalizer.Parameters(id);
        try {
            vuforiaParams.vuforiaLicenseKey = Files.toString(new File("/sdcard/robot/vuforia.key"), Charset.forName("utf-8"));
        } catch (IOException e) {
            throw new RuntimeIOException("Can't read the license key", e);
        }

        vuforiaParams.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        return ClassFactory.createVuforiaLocalizer(vuforiaParams);
    }

    @NonNull
    private VuforiaTrackables obtainVuforiaTrackableObjects(VuforiaLocalizer vuforia) {
        VuforiaTrackables vuforiaTrackables = vuforia.loadTrackablesFromAsset("FTC_2016-17");

        // Set names of trackables
        vuforiaTrackables.get(VUF_WHEEL_TARGET).setName("Wheels");
        vuforiaTrackables.get(VUF_TOOL_TARGET).setName("Tools");
        vuforiaTrackables.get(VUF_LEGOS_TARGET).setName("Legos");
        vuforiaTrackables.get(VUF_GEARS_TARGET).setName("Gears");
        return vuforiaTrackables;
    }

    private void positionTargets(VuforiaTrackables vuforiaTrackables) {
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
        final OpenGLMatrix gearsFieldLocation = OpenGLMatrix
                /* Then we translate the target off to the RED WALL. Our translation here
                is a negative translation in X.*/
                .translation((float) -mmFTCFieldWidth / 2, (float) (-12 * mmPerInch), 0)
                .multiplied(Orientation.getRotationMatrix(
                        /* First, in the fixed (field) coordinate system, we rotate 90deg in X, then 90 in Z */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        vuforiaTrackables.get(VUF_GEARS_TARGET).setLocation(gearsFieldLocation);
        final OpenGLMatrix toolsFieldLocation = OpenGLMatrix
                /* Then we translate the target off to the RED WALL. Our translation here
                is a negative translation in X.*/
                .translation((float) -mmFTCFieldWidth / 2, (float) ((24 + 12) * mmPerInch), 0)
                .multiplied(Orientation.getRotationMatrix(
                        /* First, in the fixed (field) coordinate system, we rotate 90deg in X, then 90 in Z */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        vuforiaTrackables.get(VUF_TOOL_TARGET).setLocation(toolsFieldLocation);
        final OpenGLMatrix wheelsLocationOnField = OpenGLMatrix
                /* Then we translate the target off to the Blue Audience wall.
                Our translation here is a positive translation in Y.*/
                .translation((float) (12 * mmPerInch), (float) (mmFTCFieldWidth / 2), 0)
                .multiplied(Orientation.getRotationMatrix(
                        /* First, in the fixed (field) coordinate system, we rotate 90deg in X */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 0, 0));
        vuforiaTrackables.get(VUF_WHEEL_TARGET).setLocation(wheelsLocationOnField);
        final OpenGLMatrix legosLocationOnField = OpenGLMatrix
                /* Then we translate the target off to the Blue Audience wall.
                Our translation here is a positive translation in Y.*/
                .translation( (-24 - 12) * (float) mmPerInch, (float) mmFTCFieldWidth / 2, 0)
                .multiplied(Orientation.getRotationMatrix(
                        /* First, in the fixed (field) coordinate system, we rotate 90deg in X */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 0, 0));
        vuforiaTrackables.get(VUF_LEGOS_TARGET).setLocation(legosLocationOnField);

        // Assume the phone is at the center of the robot
        final OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
                .translation((float) mmBotWidth / 2f, (float) mmBotWidth / 2f, (float) mmBotWidth / 2f)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.YZY,
                        AngleUnit.DEGREES, -90, 0, 0));

        for (VuforiaTrackable trackable : vuforiaTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(phoneLocationOnRobot, VuforiaLocalizer.CameraDirection.BACK);
        }
    }

    void activate() {
        synchronized (watcher) {
            vuforiaTrackables.activate();
        }
    }

    void deactivate() {
        synchronized (watcher) {
            vuforiaTrackables.deactivate();
            watcher.resetWatcher();
        }
    }

    boolean hasUsableData() {
        synchronized (watcher) {
            return watcher.usableData;
        }
    }

    long lastUpdate() {
        synchronized (watcher) {
            return watcher.lastTimestamp;
        }
    }

    Position currentLocation() {
        synchronized (watcher) {
            lock();
            try {
                checkState(hasUsableData(), "position is unavailable");
                final OpenGLMatrix lastLocation = watcher.lastLocation;
                final Position position = getPosition(lastLocation);
                return position;
            } finally {
                unlock();
            }
        }
    }

    @NonNull
    private Position getPosition(OpenGLMatrix lastLocation) {
        return new Position(MM, lastLocation.get(0, 0), lastLocation.get(0, 1), lastLocation.get(0, 2), lastUpdate());
    }

    @NonNull
    String visibleTrackable() {
        return watcher.currentTrackable;
    }

    Runnable loop() {
        return watcher;
    }

    public Velocity velocity() {
        Position currentLocation = currentLocation();
        Position lastLocation = getPosition(watcher.oldLocation);
        final long dt = currentLocation.acquisitionTime - lastLocation.acquisitionTime;
        return new Velocity(MM,
                (currentLocation.x - lastLocation.x) / dt,
                (currentLocation.y - lastLocation.y) / dt,
                (currentLocation.z - lastLocation.z) / dt,
                watcher.lastTimestamp);
    }

    public void unlock() {
        watcher.unlock();
    }

    public void lock() {
        watcher.lock();
    }

    private static class CameraWatcher implements Runnable {
        private static CameraWatcher instance;
        private final Semaphore semaphore = new Semaphore(0, true);
        private final VuforiaTrackables trackables;
        private long lastTimestamp;
        private OpenGLMatrix lastLocation;
        private OpenGLMatrix oldLocation;
        private String currentTrackable;
        private boolean usableData = false;

        private CameraWatcher(@NotNull VuforiaTrackables trackables) {
            checkState(instance == null, "CameraWatcher has been initialized");
            this.trackables = trackables;
        }

        static CameraWatcher getInstance(@NotNull VuforiaTrackables trackables) {
            if (instance == null) instance = new CameraWatcher(trackables);
            return instance;
        }

        void resetWatcher() {
            lastTimestamp = 0;
            lastLocation = null;
            currentTrackable = null;
            usableData = false;
        }

        @Override
        public void run() {
            boolean locationUpdated = false;
            lock();
            synchronized (this) {
                for (VuforiaTrackable trackable : trackables) {
                    VuforiaTrackableDefaultListener listener = (VuforiaTrackableDefaultListener) trackable.getListener();
                    if (listener.isVisible()) currentTrackable = trackable.getName();
                    OpenGLMatrix updatedRobotLocation = listener.getUpdatedRobotLocation();
                    if (updatedRobotLocation != null) {
                        oldLocation = lastLocation;
                        lastLocation = updatedRobotLocation;
                        lastTimestamp = System.nanoTime();
                        locationUpdated = true;
                    }
                }

                usableData = locationUpdated;
            }
            unlock();
        }

        public void lock() {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void unlock() {
            semaphore.release();
        }
    }
}
