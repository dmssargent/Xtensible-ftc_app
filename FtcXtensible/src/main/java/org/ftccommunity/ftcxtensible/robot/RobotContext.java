/*
 * Copyright © 2015 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 *  do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 *  BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible.robot;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

import org.ftccommunity.bindings.DataBinder;
import org.ftccommunity.ftcxtensible.hardware.camera.ExtensibleCameraManager;
import org.ftccommunity.ftcxtensible.interfaces.AbstractRobotContext;
import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;
import org.ftccommunity.ftcxtensible.networking.ServerSettings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Beta
@NotDocumentedWell
public class RobotContext implements AbstractRobotContext {
    private ExtensibleHardwareMap hardwareMap;
    private HardwareMap basicHardwareMap;

    private Context appContext;

    private Telemetry telemetry;
    private ExtensibleTelemetry extensibleTelemetry;

    private Gamepad gamepad1;
    private ExtensibleGamepad extensibleGamepad1;
    private Gamepad gamepad2;
    private ExtensibleGamepad extensibleGamepad2;

    private ServerSettings serverSettings;
    private RobotStatus status;
    private ExtensibleCameraManager extensibleCameraManager;

    private NetworkedOpMode networkedOpMode;
    private boolean networkingEnabled;

    private LinkedList<InterfaceHttpData> postedData;

    private ExecutorService asyncService;
    private RobotLogger logger;

    private DataBinder bindings;
    private View layout;

    private RobotContext() {
        serverSettings = ServerSettings.createServerSettings();
        status = new RobotStatus(this, RobotStatus.LogTypes.HTML);
        postedData = new LinkedList<>();

        extensibleGamepad1 = new ExtensibleGamepad();
        extensibleGamepad2 = new ExtensibleGamepad();

        asyncService = MoreExecutors.listeningDecorator(
                Executors.newCachedThreadPool());
        extensibleCameraManager = new ExtensibleCameraManager(this, 100);
        logger = new RobotLogger(this);

        bindings = DataBinder.getInstance();
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
        basicHardwareMap = hwMap;

        appContext = hwMap.appContext;
        if (appContext == null) {
            appContext = buildApplicationContext();
        }
        telemetry = tlmtry;
        extensibleTelemetry = new ExtensibleTelemetry(tlmtry);
    }

    public static Context buildApplicationContext() {
        try {
            final Class<?> activityThreadClass =
                    Class.forName("android.app.ActivityThread");
            final Method method = activityThreadClass.getMethod("currentApplication");

            return (Context) method.invoke(null, (Object[]) null);
        } catch (Exception ex) {
            Log.e("ROBOT_CONTEXT::", "Cannot build app context! It will be null.", ex);
            return null;
        }
    }

    @Override
    public RobotContext enableNetworking() {
        if (networkedOpMode == null) {
            networkedOpMode = new NetworkedOpMode(this);
        }
        networkingEnabled = true;

        return this;
    }

    @Override
    public RobotContext disableNetworking() {
        if (!networkingEnabled || networkedOpMode == null) {
            throw new IllegalStateException("Networking is already disabled!");
        }

        networkedOpMode.stopServer();
        networkedOpMode = null;

        networkingEnabled = false;
        return this;
    }

    /**
     * Re-generates the {@link ExtensibleHardwareMap} form of a hardware map based on a given
     * correct {@link HardwareMap}. This function has the same warning applied as {@link ExtensibleHardwareMap#rebuild(HardwareMap)}
     *
     * @param map a correct non-null {@code HardwareMap}
     */
    @Override
    public void bindHardwareMap(@NotNull HardwareMap map) {
        this.basicHardwareMap = checkNotNull(map);
        rebuildHardwareMap();
    }

    /**
     * Rebuilds the {@link ExtensibleHardwareMap} based on the current instance off the HardwareMap
     * previously provided during instantiation, or by {@link #bindHardwareMap(HardwareMap)}.
     */
    @Override
    public void rebuildHardwareMap() {
        hardwareMap().rebuild(basicHardwareMap);
    }

    @Override
    public RobotContext startNetworking() {
        if (networkingEnabled && networkedOpMode == null) {
            throw new IllegalStateException("Networking is disabled!");
        }

        networkedOpMode.startServer();
        return this;
    }

    @Override
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
    @Override
    public ExtensibleHardwareMap hardwareMap() {
        return hardwareMap;
    }

    /**
     * Binds a new activity context to this
     *
     * @param context a non-null activity context
     * @throws IllegalStateException, IllegalArgumentException
     */
    @Override
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

    @Override
    public void prepare(Context ctx) {
        checkArgument(ctx instanceof Activity, "Invalid context; it must be of an activity context type");
        bindAppContext(ctx);

        layout = ((Activity) appContext()).findViewById(controllerBindings().getIntegers().get("ftcview"));
    }

    /**
     * Returns an App Context
     *
     * @return the Android Context
     */
    @Override
    public Context appContext() {
        return appContext;
    }

    /**
     * Returns opMode reference to the first gamepad
     *
     * @return the first gamepad
     * @deprecated Please use the gamepad1 instead
     */
    @Override
    @Deprecated
    public Gamepad legacyGamepad1() {
        return gamepad1;
    }

    /**
     * Returns opMode reference to the second gampepad
     *
     * @return the second gamepad
     * @deprecated Please use the gamepad2 instead
     */
    @Override
    @Deprecated
    public Gamepad legacyGamepad2() {
        return gamepad2;
    }

    /**
     * The current HTTP server settings
     *
     * @return <code>ServerSettings</code>
     */
    @Override
    public ServerSettings serverSettings() {
        return serverSettings;
    }

    /**
     * An tool for writing to multiple the same message to multiple places
     *
     * @return the current {@link RobotLogger} to use
     */
    @Override
    public RobotLogger log() {
        return logger;
    }

    /**
     * Submit a Runnable to be ran
     *
     * @param runnable Runnable to run at some time
     */
    @Override
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
    @Override
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
    @Override
    public RobotStatus status() {
        return status;
    }

    /**
     * Add a collection of data received for further processing by user code
     *
     * @param datas a collection of type <code>InterfaceHttpData</code> to be added
     */
    @Override
    public void addPostData(Collection<InterfaceHttpData> datas) {
        postedData.addAll(datas);
    }

    /**
     * Get a copy of the received data for use
     *
     * @return an <code>ImmutableList</code> that is a copy of the data received via networking
     */
    @Override
    public ImmutableList<InterfaceHttpData> getPostedData() {
        return ImmutableList.copyOf(postedData);
    }

    /**
     * The telemetry object to be used
     *
     * @return the telemetry as provide by the FTC SDK
     */
    @Override
    public ExtensibleTelemetry telemetry() {
        return extensibleTelemetry;
    }

    /**
     * The first Gamepad in the form of an ExtensibleGamepad.
     *
     * @return the first Gamepad (gamepad1) cast to an ExtensibleGamepad
     */
    @Override
    public ExtensibleGamepad gamepad1() {
        return extensibleGamepad1;
    }

    /**
     * The second Gamepad in the form of an ExtensibleGamepad
     *
     * @return the second gamepad (gampad2) cast to an ExtensibleGamepad
     */
    @Override
    public ExtensibleGamepad gamepad2() {
        return extensibleGamepad2;
    }

    @Override
    public ExtensibleCameraManager cameraManager() {
        return extensibleCameraManager;
    }

    @Override
    public void release() {
        if (extensibleCameraManager != null) {
            extensibleCameraManager.stop();
            extensibleCameraManager = null;
        }

        if (networkedOpMode != null) {
            disableNetworking();
        }

        if (asyncService != null) {
            asyncService.shutdown();
        }

        if (extensibleTelemetry != null) {
            try {
                extensibleTelemetry.close();
            } catch (IOException e) {
                Log.wtf("ROBOT_CONTEXT::", e);
            }
            extensibleTelemetry = null;
        }
    }

    /**
     * Gets the robot controller data bindings
     *
     * @return the robot controller data binding
     */
    @Override
    @NotNull
    public DataBinder controllerBindings() {
        return bindings;
    }

    /**
     * Gets the recommended robot controller view area
     *
     * @return a View representing the real estate that Qualcomm has set aside
     */
    @Override
    @NotNull
    public View robotControllerView() {
        return layout;
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
