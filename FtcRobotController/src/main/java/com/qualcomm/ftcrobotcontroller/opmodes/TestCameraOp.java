package com.qualcomm.ftcrobotcontroller.opmodes;

import android.hardware.Camera;
import android.view.SurfaceView;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkApiLevel;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkVersion;


@Autonomous
@RobotSdkVersion(RobotSdkApiLevel.R1_2015)
public class TestCameraOp extends OpMode {
    Camera camera;
    SurfaceView surfaceView;

    @Override
    public void init() {

    }

    @Override
    public void loop() {

    }
}
