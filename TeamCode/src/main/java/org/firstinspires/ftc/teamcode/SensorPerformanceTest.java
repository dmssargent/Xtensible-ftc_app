package org.firstinspires.ftc.teamcode;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;


@TeleOp
public class SensorPerformanceTest extends SimpleOpMode {
    private BufferedWriter fileWriter;
    private long lastLoopTime = System.nanoTime();
    private ClutchHardware hardware;

    @Override
    public void init(RobotContext ctx) throws Exception {
        File file = new File("/sdcard/robots/sensorData.csv");
        file.getParentFile().mkdirs();
        fileWriter = Files.newWriter(file, Charsets.UTF_8);
        hardware = new ClutchHardware(this);
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        long deltaTime = System.nanoTime() - lastLoopTime;
        String data = deltaTime + ",";
        data += hardware.colorSensor.blue() + ",";
        data += hardware.colorSensor.red() + ",";
        data += hardware.distanceSensor.getDistance(DistanceUnit.CM) + ",";
        data += hardware.opticalDistanceSensor.getRawLightDetected() + ",";
        fileWriter.write(data);
    }

    @Override
    public void stop(RobotContext context, LinkedList<Object> out) {
        try {
            fileWriter.flush();
        } catch (IOException e) {
            RobotLog.e("File failed to write");
            RobotLog.logStacktrace(e);
        }
    }
}
