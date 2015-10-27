package org.ftc.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AccelerationSensor;

public class AccelerationSensorExample extends OpMode {
    private AccelerationSensor sensor;

    @Override
    public void init() {
        sensor = hardwareMap.accelerationSensor.get("acclSensor");
    }

    @Override
    public void loop() {
        AccelerationSensor.Acceleration currentAcceleration = sensor.getAcceleration();

        double gForceInXAxis = currentAcceleration.x;
        double gForceInYAxis = currentAcceleration.y;
        double gForceInZAxis = currentAcceleration.z;

        telemetry.addData("ACCL:", "Current acceleration: x - " + gForceInXAxis + " y - " +
                gForceInYAxis + " z - " + gForceInZAxis);
    }
}
