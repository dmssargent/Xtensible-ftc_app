package org.ftccommunity.networkedopmode;

import android.content.Context;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.ftccommunity.networkedopmode.networking.ServerSettings;
import org.ftccommunity.networkedopmode.robot.RobotStatus;

public class RobotContext {
    private HardwareMap hardwareMap;
    private Context appContext;

    //private HashMap<HardwareDevice, LinkedList<UpstreamHardwareUpdate<HardwareDevice>>> deviceUpstreamPipeline;
    // private HashMap<Collection<HardwareDevice>, LinkedList<UpstreamHardwareUpdate>> genericUpstreamPipeline;
    private Gamepad gamepad1;
    private Gamepad gamepad2;

    private ServerSettings serverSettings;
    private RobotStatus status;

    private RobotContext(HardwareMap hwMap) {
//        deviceUpstreamPipeline = new HashMap<HardwareDevice, LinkedList<UpstreamHardwareUpdate<HardwareDevice>>>();
//        genericUpstreamPipeline = new HashMap<Collection<HardwareDevice>, LinkedList<UpstreamHardwareUpdate>>();
        serverSettings = ServerSettings.createServerSettings();
        status = new RobotStatus(this, RobotStatus.LogTypes.HTML);
    }

    public RobotContext(Gamepad gp1, Gamepad gp2, HardwareMap hwMap) {
        this(hwMap);
        gamepad1 = gp1;
        gamepad2 = gp2;
        hardwareMap = hwMap;
        appContext = hwMap.appContext;
    }


    public HardwareMap getHardwareMap() {
        return hardwareMap;
    }

    public Context getAppContext() {
        return appContext;
    }

    public Gamepad getGamepad1() {
        return gamepad1;
    }

    public Gamepad getGamepad2() {
        return gamepad2;
    }

    public ServerSettings getServerSettings() {
        return serverSettings;
    }

    public RobotStatus getStatus() {
        return status;
    }

   /* public LinkedList upstreamPipeline(HardwareDevice device) {
        if (!deviceUpstreamPipeline.containsKey(device)) {
            deviceUpstreamPipeline.put(device, new LinkedList<UpstreamHardwareUpdate>());
        }
        return deviceUpstreamPipeline.get(device);
    }

    public LinkedList upstreamPipeline(Collection<HardwareDevice> devices) {
        if (!genericUpstreamPipeline.containsKey(devices)) {
            genericUpstreamPipeline.put(devices, new LinkedList<UpstreamHardwareUpdate>());
        }
        return genericUpstreamPipeline.get(devices);
    }

    public void processDevice(HardwareDevice device) {
        if (deviceUpstreamPipeline.containsKey(device)) {
            for (UpstreamHardwareUpdate<HardwareDevice> upstreamHardwareUpdate : deviceUpstreamPipeline.get(device)) {
                upstreamHardwareUpdate.processRead(device, );
            }
        }
    }*/
}
