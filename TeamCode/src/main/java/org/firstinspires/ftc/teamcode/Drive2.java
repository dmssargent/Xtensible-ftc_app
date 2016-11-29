package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.clutchauto.nav.Navigation;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

@TeleOp
public class Drive2 extends SimpleOpMode {
    //private Navigation navigation;
    private DriveTrain driveTrain;
    private OpticalDistanceSensor opticalDistanceSensor;
    private UltrasonicSensor distanceSensor;
    private DcMotor winch;

    @Override
    public void init(RobotContext ctx) throws Exception {
        //navigation = new Navigation(ctx.appContext());
//        leftFront = hardwareMap.dcMotor("leftFront");
//        leftRear = hardwareMap.dcMotor("leftRear");
//        rightFront = hardwareMap.dcMotor("rightFront");rd
//        rightRear = hardwareMap.dcMotor("rightRear");
        winch = hardwareMap.get("winch");
        driveTrain = new DriveTrain(gamepad1, null, this, "leftFront", "rightFront", "leftRear", "rightRear");
        try {
            opticalDistanceSensor = hardwareMap.opticalDistanceSensors().get("opticalDistance");
        } catch (IllegalArgumentException ex) {
            RobotLog.i("Can't obtain Optical Distance sensor; \"opticalDistance\"");
        }
        try {
            distanceSensor = hardwareMap.ultrasonicSensors().get("ultrasonic");
        } catch (IllegalArgumentException ex) {
            RobotLog.i("Can't obtain Ultrasonic sensor; \"ultrasonic\"");
        }
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        driveTrain.updateTargetWithGamepad();

        if (gamepad1.dpad.isUpPressed()) {
            winch.setPower(.9);
        } else if (gamepad1.dpad.isDownPressed()) {
            winch.setPower(-.9);
        } else {
            winch.setPower(0);
        }
        try {
            telemetry.data("ODS_READ", opticalDistanceSensor == null ? "No Sensor " : opticalDistanceSensor.getRawLightDetected());
            telemetry.data("DIS_READ", distanceSensor == null ? "No Sensor" : distanceSensor.getUltrasonicLevel());
        } catch (NullPointerException ignored) {
        }
    }
}
