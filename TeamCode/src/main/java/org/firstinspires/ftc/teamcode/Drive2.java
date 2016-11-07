package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import org.firstinspires.ftc.teamcode.clutchauto.nav.Navigation;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;


public class Drive2 extends SimpleOpMode {
    private Navigation navigation;
    private DriveTrain driveTrain;
    private OpticalDistanceSensor opticalDistanceSensor;
    private UltrasonicSensor distanceSensor;

    @Override
    public void init(RobotContext ctx) throws Exception {
        navigation = new Navigation(ctx.appContext());
//        leftFront = hardwareMap.dcMotor("leftFront");
//        leftRear = hardwareMap.dcMotor("leftRear");
//        rightFront = hardwareMap.dcMotor("rightFront");
//        rightRear = hardwareMap.dcMotor("rightRear");
        driveTrain = new DriveTrain(gamepad1, null, this, "leftFront", "leftRear", "rightFront", "rightRear");
        opticalDistanceSensor = hardwareMap.opticalDistanceSensors().get("opticalDistance");
        distanceSensor = hardwareMap.ultrasonicSensors().get("ultrasonic");
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        driveTrain.updateTargetWithGamepad();
        telemetry.data("NAV_POS", navigation.position());
        telemetry.data("NAV_VEL", navigation.velocity());
        telemetry.data("ODS_READ", opticalDistanceSensor.getRawLightDetected());
        telemetry.data("DIS_READ", distanceSensor.getUltrasonicLevel());
    }
}
