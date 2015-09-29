/*
 * Copyright © 2015 David Sargent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible.robot;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

import org.ftccommunity.ftcxtensible.networking.ServerSettings;
import org.ftccommunity.ftcxtensible.sensors.camera.CameraManager;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.handler.codec.http.multipart.InterfaceHttpData;

public class RobotContext {
    private ExtensibleHardwareMap hardwareMap;
    private Context appContext;
    private Telemetry telemetry;

    private Gamepad gamepad1;
    private ExtensibleGamepad extensibleGamepad1;
    private Gamepad gamepad2;
    private ExtensibleGamepad extensibleGamepad2;

    private ServerSettings serverSettings;
    private RobotStatus status;
    private CameraManager cameraManager;

    private NetworkedOpMode networkedOpMode;
    private boolean networkingEnabled;

    private LinkedList<InterfaceHttpData> postedData;

    private ExecutorService asyncService;
    private RobotLogger logger;

    private RobotContext() {
        serverSettings = ServerSettings.createServerSettings();
        status = new RobotStatus(this, RobotStatus.LogTypes.HTML);
        postedData = new LinkedList<>();

        extensibleGamepad1 = new ExtensibleGamepad();
        extensibleGamepad2 = new ExtensibleGamepad();

        asyncService = MoreExecutors.listeningDecorator(
                Executors.newCachedThreadPool(MoreExecutors.platformThreadFactory()));
        cameraManager = new CameraManager(this);
        logger = new RobotLogger(this);
    }

    /**
     * Generates OpMode Robot Context item bonded to the normal items in an OpMode
     *
     * @param gp1   opMode reference to Gamepad 1
     * @param gp2   opMode reference to Gamepad 2
     * @param hwMap opMode reference to the Hardware Map
     */
    public RobotContext(Gamepad gp1, Gamepad gp2, HardwareMap hwMap, Telemetry tlmtry) {
        this();
        if (gp1 == null || gp2 == null || hwMap == null) {
            throw new NullPointerException();
        }

        gamepad1 = gp1;
        gamepad2 = gp2;
        hardwareMap = new ExtensibleHardwareMap(hwMap);
        appContext = hwMap.appContext;
        if (appContext == null) {
            try {
                final Class<?> activityThreadClass =
                        Class.forName("android.app.ActivityThread");
                final Method method = activityThreadClass.getMethod("currentApplication");

                appContext = (Context) method.invoke(null, (Object[]) null);
            } catch (Exception ex) {
                Log.e("ROBOT_CONTEXT::", "Cannot build app context! It will be null.", ex);
            }
        }
        telemetry = tlmtry;
    }

    public RobotContext enableNetworking() {
        if (networkedOpMode == null) {
            networkedOpMode = new NetworkedOpMode(this);
        }
        networkingEnabled = true;

        return this;
    }

    public RobotContext disableNetworking() {
        if (!networkingEnabled || networkedOpMode == null) {
            throw new IllegalStateException("Networking is already disabled!");
        }

        networkedOpMode.stopServer();
        networkedOpMode = null;

        networkingEnabled = false;
        return this;
    }

    public RobotContext startNetworking() {
        if (networkingEnabled && networkedOpMode == null) {
            throw new IllegalStateException("Networking is disabled!");
        }

        networkedOpMode.startServer();
        return this;
    }

    public RobotContext stopNetworking() {
        if (networkingEnabled && networkedOpMode == null) {
            throw new IllegalStateException("Networking is disabled!");
        }

        networkedOpMode.stopServer();
        return this;
    }

    /**
     * Returns the Hardware Map     *
     *
     * @return the <code>ExtensibleHardwareMap</code> currently in use
     */
    public ExtensibleHardwareMap hardwareMap() {
        return hardwareMap;
    }

    /**
     * Binds a new activity context to this
     *
     * @param context a non-null activity context
     * @throws IllegalStateException, IllegalArgumentException
     */
    public void bindAppContext(Context context) throws IllegalArgumentException, IllegalStateException {
        if (context instanceof Activity) {
            /*if (appContext != null) {
                throw new IllegalStateException("Cannot rebind a context when the current context" +
                        " is not null");
            }*/
            appContext = context;
        } else {
            throw new IllegalArgumentException("Invalid context; it must be of an activity context type");
        }
    }

    /**
     * Returns an App Context
     *
     * @return the Android Context
     */
    public Context getAppContext() {
        return appContext;
    }

    /**
     * Returns opMode reference to the first gamepad
     *
     * @return the first gamepad
     * @deprecated Please use the xGamepad1 instead
     */
    @Deprecated
    public Gamepad getGamepad1() {
        return gamepad1;
    }

    /**
     * Returns opMode reference to the second gampepad
     *
     * @return the second gamepad
     * @deprecated Please use the xGamepad2 instead
     */
    @Deprecated
    public Gamepad getGamepad2() {
        return gamepad2;
    }

    /**
     * The current HTTP server settings
     *
     * @return <code>ServerSettings</code>
     */
    public ServerSettings getServerSettings() {
        return serverSettings;
    }

    /**
     * An tool for writing to multiple the same message to multiple places
     *
     * @return the current {@link RobotLogger} to use
     */
    public RobotLogger log() {
        return logger;
    }

    /**
     * Submit a Runnable to be ran
     *
     * @param runnable Runnable to run at some time
     */
    public void submitAsyncTask(Runnable runnable) {
        if (runnable != null) {
            asyncService.execute(runnable);
        } else {
            throw new  NullPointerException();
        }
    }

    /**
     * Run a {@code Runnable} on the main app thread
     *
     * @param runnable Runnable to run on the UI Thread.
     */
    public void runOnUiThread(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException();
        }

        ((Activity) appContext).runOnUiThread(runnable);
    }

    /**
     * The Robot Status Object
     *
     * @return the current status of the robot
     */
    public RobotStatus status() {
        return status;
    }

    /**
     * Add a collection of data received for further processing by user code
     *
     * @param datas a collection of type <code>InterfaceHttpData</code> to be added
     */
    public void addPostData(Collection<InterfaceHttpData> datas) {
        postedData.addAll(datas);
    }

    /**
     * Get a copy of the received data for use
     *
     * @return an <code>ImmutableList</code> that is a copy of the data received via networking
     */
    public ImmutableList<InterfaceHttpData> getPostedData() {
        return ImmutableList.copyOf(postedData);
    }

    /**
     * The telemetry object to be used
     *
     * @return the telemetry as provide by the FTC SDK
     */
    public Telemetry telemetry() {
        return telemetry;
    }

    /**
     * The first Gamepad in the form of an ExtensibleGamepad.
     *
     * @return the first Gamepad (gamepad1) cast to an ExtensibleGamepad
     */
    public ExtensibleGamepad xGamepad1() {
        return extensibleGamepad1;
    }

    /**
     * The second Gamepad in the form of an ExtensibleGamepad
     *
     * @return the second gamepad (gampad2) cast to an ExtensibleGamepad
     */
    public ExtensibleGamepad xGamepad2() {
        return extensibleGamepad2;
    }

    public CameraManager cameraManager() {
        return cameraManager;
    }

    public void release() {
        if (cameraManager != null) {
            cameraManager.stop();
            cameraManager = null;
        }

        if (networkedOpMode != null) {
            disableNetworking();
        }

        if (asyncService != null) {
            asyncService.shutdown();
        }
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
