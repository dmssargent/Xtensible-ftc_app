import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Edrich on 11/1/2016.
 */
public class Encoders extends LinearOpMode {
    //Declare motors
    private DcMotor driveLeft; // lower camel-case field names, made private
    private DcMotor driveRight;

    // double Ticks;

    public void runOpMode() throws InterruptedException {
        initSystem();
        waitForStart();

        while (opModeIsActive()) {
            driveForwardDistance(0.2, 10); // lower camel case method names
            Thread.sleep(1000);
            driveForwardDistance(0.2, -10);
        }
    }

    // you don't need to make this method public, declare methods in the minimalistic scope possible
    private void initSystem() throws InterruptedException {
        driveLeft = hardwareMap.dcMotor.get("driveLeft");
        driveRight = hardwareMap.dcMotor.get("driveRight");
        driveRight.setDirection(DcMotor.Direction.REVERSE);
        driveLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Use a single blank line to separate logical groups
        double driveSpeed = 0;
        // Brake left and right drive motors
        driveLeft.setPower(driveSpeed);
        driveRight.setPower(driveSpeed);

        // // The above can be replaced with the following line:
        // stopRobot();
    }


    private void driveForwardDistance(double power, int distance) throws InterruptedException {
        // Reset encoders
        driveLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Change to run to position prior to setting target
        // Run to position
        driveLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        driveRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set target position
        driveLeft.setTargetPosition(distance);
        driveRight.setTargetPosition(distance);

        // Set drive power
        driveForward(power);

        while (driveLeft.isBusy() && driveRight.isBusy()) {
            // wait until position is reached
            idle(); // prevent unnecessary cpu usage, since we don't need to constantly poll the motors
            if (!opModeIsActive() || Thread.currentThread().isInterrupted()) {
                throw new InterruptedException(); // this is the simplest method to not crash your robot controller, due to this loop
            }
        }


        //stopRobot and change modes back to normal
        stopRobot();
        driveLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    private void driveForward(double power) {
        driveLeft.setPower(power);
        driveRight.setPower(power);
    }

    // stop means stop, you shouldn't need to specify the power to stopRobot
    // since stop is in use by FIRST, rename this to stop robot
    private void stopRobot() {
        driveLeft.setPower(0);
        driveRight.setPower(0);
    }

    public void turnLeft(double power) {
        driveLeft.setPower(-power);
        driveRight.setPower(power);
    }

    public void turnRight(double power) {
        driveLeft.setPower(power);
        driveRight.setPower(-power);
    }
} 