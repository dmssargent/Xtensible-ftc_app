package org.ftc.opmodes;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.RelativeLayout;

import org.ftccommunity.ftcxtensible.RobotContext;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;

import java.util.LinkedList;

@Autonomous
public class CameraOpMode extends ExtensibleOpMode {
    CameraOpMode camera;
    RelativeLayout rcLayout;

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void init(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void onSuccess(RobotContext ctx, Object event, Object in) {

    }

    @Override
    public int onFailure(RobotContext ctx, Type eventType, Object event, Object in) {
        return -1;
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /** A safe way to get an instance of the CameraOpMode object. */
    public static CameraOpMode getCameraInstance(){
        CameraOpMode c = null;
        try {
            c = CameraOpMode.open(); // attempt to get a CameraOpMode instance
        }
        catch (Exception e){
            // CameraOpMode is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
