package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.clutchauto.nav.Navigation;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

@TeleOp
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
        telemetry.data("NAV_POS", navigation.position());
        telemetry.data("NAV_VEL", navigation.velocity());
        telemetry.data("ODS_READ", opticalDistanceSensor == null ? "No Sensor " : opticalDistanceSensor.getRawLightDetected());
        telemetry.data("DIS_READ", distanceSensor == null ? "No Sensor" : distanceSensor.getUltrasonicLevel());
    }
}
