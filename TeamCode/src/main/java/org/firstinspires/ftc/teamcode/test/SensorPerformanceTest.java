package org.firstinspires.ftc.teamcode.test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@TeleOp
public class SensorPerformanceTest extends SimpleOpMode {
    private BufferedWriter fileWriter;
    private long lastLoopTime = System.nanoTime();
    private List<HardwareDevice> sensors = new ArrayList<>();

    @Override
    public void init(RobotContext ctx) throws Exception {
        File file = new File("/sdcard/robots/sensorData.csv");
        file.getParentFile().mkdirs();
        fileWriter = Files.newWriter(file, Charsets.UTF_8);
        sensors.addAll(hardwareMap.colorSensors().values());
        sensors.addAll(hardwareMap.ultrasonicSensors().values());
        sensors.addAll(hardwareMap.analogInputs().values());
        sensors.addAll(hardwareMap.digitalChannels().values());
        sensors.addAll(hardwareMap.irSeekerSensors().values());

        StringBuilder builder = new StringBuilder();
        for (HardwareDevice device : sensors) {
            builder.append(device.toString());
            if (device instanceof ColorSensor) {
                for (int i = 0; i < 4; i++) {
                    builder.append(',');
                }
            }
        }
        fileWriter.write(builder.toString());
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        long deltaTime = System.nanoTime() - lastLoopTime;
        String data = String.valueOf(deltaTime);
        for (HardwareDevice device : sensors) {
            if (device instanceof ColorSensor) { // 4 fields
                ColorSensor colorSensor = (ColorSensor) device;
                data += ",";
                data += colorSensor.blue() + ",";
                data += colorSensor.red() + ",";
                data += colorSensor.green() + ",";
                data += colorSensor.alpha();
            } else if (device instanceof UltrasonicSensor) { // 1 field
                UltrasonicSensor ultrasonicSensor = (UltrasonicSensor) device;
                data += "," + ultrasonicSensor.getUltrasonicLevel();
            } else if (device instanceof DistanceSensor) { // 1 field
                DistanceSensor ultrasonicSensor = (DistanceSensor) device;
                data += "," + ultrasonicSensor.getDistance(DistanceUnit.CM);
            } else if (device instanceof OpticalDistanceSensor) { // 1 field
                OpticalDistanceSensor ultrasonicSensor = (OpticalDistanceSensor) device;
                data += "," + ultrasonicSensor.getRawLightDetected();
            }
        }

        fileWriter.newLine();
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
