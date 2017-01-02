/*
 * Copyright © 2016 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ftccommunity.ftcxtensible.robot;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.ftccommunity.bindings.DataBinder;
import org.ftccommunity.ftcxtensible.hardware.camera.ExtensibleCameraManager;
import org.ftccommunity.ftcxtensible.interfaces.AbstractRobotContext;
import org.ftccommunity.ftcxtensible.interfaces.RobotAction;
import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;
import org.ftccommunity.ftcxtensible.networking.ServerSettings;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Beta
@NotDocumentedWell
public class RobotContext implements AbstractRobotContext {
    private final ServerSettings serverSettings;
    private final RobotStatus status;
    //private final LinkedList<InterfaceHttpData> postedData;
    //private List<Thread> asyncsRunning;
    private final ExecutorService asyncService;
    private final RobotLogger logger;
    private final DataBinder bindings;
    private final RobotEventManager eventManager;
    private ExtensibleHardwareMap hardwareMap;
    private HardwareMap basicHardwareMap;
    private Context appContext;
    private ExtensibleTelemetry extensibleTelemetry;
    private Gamepad gamepad1;
    private ExtensibleGamepad extensibleGamepad1;
    private Gamepad gamepad2;
    private ExtensibleGamepad extensibleGamepad2;
    private ExtensibleCameraManager extensibleCameraManager;
    private NetworkedOpMode networkedOpMode;
    // private boolean networkingEnabled;
    private View layout;
    private View cameraViewParent;

    //private OpModeManager opModeManager;
    private List<RobotEvent> events = new LinkedList<>();


    RobotContext() {
        serverSettings = ServerSettings.createServerSettings();
        status = new RobotStatus(this, RobotStatus.LogTypes.HTML);
        // postedData = new LinkedList<>();

        extensibleGamepad1 = new ExtensibleGamepad();
        extensibleGamepad2 = new ExtensibleGamepad();

        asyncService = MoreExecutors.listeningDecorator(
                Executors.newCachedThreadPool());

        extensibleCameraManager = new ExtensibleCameraManager(this, 100);
        logger = RobotLogger.createInstance(this);

        bindings = DataBinder.getInstance();
        appContext = buildApplicationContext();
        eventManager = new RobotEventManager(this);

        List<Runnable> asyncs = findAsyncMethods(this.getClass(), null);
        Log.i("ASYNC_RUNNER", "Discovered " + asyncs.size() + " asyncs");
        for (Runnable runnable : asyncs) {
            Log.i("ASYNC_RUNNER", "Starting " + runnable.getClass().getSimpleName());
            asyncService.submit(runnable);
        }
    }

    @Nullable
    private static Context buildApplicationContext() {
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
//        if (networkedOpMode == null) {
//            networkedOpMode = new NetworkedOpMode(this);
//        }
//        networkingEnabled = true;
        // TODO: 12/19/2016 rebuild networking
        RobotLog.w("[xNetworking] stub!");

        return this;
    }

    @Override
    public RobotContext disableNetworking() {
//        if (!networkingEnabled || networkedOpMode == null) {
//            throw new IllegalStateException("Networking is already disabled!");
//        }
//
//        networkedOpMode.stopServer();
//        networkedOpMode = null;
//
//        networkingEnabled = false;

        // TODO: 12/19/2016 rebuild networking
        RobotLog.w("[xNetworking] stub!");
        return this;
    }

    /**
     * Re-generates the {@link ExtensibleHardwareMap} form of a hardware map based on a given
     * correct {@link HardwareMap}. This function has the same warning applied as {@link
     * ExtensibleHardwareMap#rebuild(HardwareMap)}
     *
     * @param map a correct non-null {@code HardwareMap}
     */
    @Override
    public void bindHardwareMap(@NotNull HardwareMap map) {
        this.basicHardwareMap = checkNotNull(map, "XTENSIBLE: the init-phase hardware map is null");
        if (hardwareMap == null) {
            hardwareMap = new ExtensibleHardwareMap(basicHardwareMap);
        } else {
            hardwareMap.rebuild(basicHardwareMap);
        }
    }

    /**
     * Rebuilds the {@link ExtensibleHardwareMap} based on the current instance off the HardwareMap
     * previously provided during instantiation, or by {@link #bindHardwareMap(HardwareMap)}.
     */
    @Override
    public void rebuildHardwareMap() {
        hardwareMap.rebuild(basicHardwareMap);
    }

    /**
     * Returns the underlying HardwareMap, in case of the {@link ExtensibleHardwareMap} is not
     * functioning correctly
     *
     * @return the {@link HardwareMap} that is used internally
     */
    @Override
    @Deprecated
    public HardwareMap legacyHardwareMap() {
        return basicHardwareMap;
    }

    @Override
    public RobotContext startNetworking() {
//        if (networkingEnabled && networkedOpMode == null) {
//            throw new IllegalStateException("Networking is disabled!");
//        }
//
//        networkedOpMode.startServer();
        // TODO: 12/19/2016 rebuild networking
        RobotLog.w("[xNetworking] stub!");
        return this;
    }

    @Override
    public RobotContext stopNetworking() {
//        if (networkingEnabled && networkedOpMode == null) {
//            throw new IllegalStateException("Networking is disabled!");
//        }
//
//        networkedOpMode.stopServer();
        // TODO: 12/19/2016 rebuild networking
        RobotLog.w("[xNetworking] stub!");
        return this;
    }

    /**
     * Returns the Hardware Map
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
            appContext = context;
        } else {
            throw new IllegalArgumentException("Invalid context; it must be of an activity context type");
        }
    }

    @Override
    public void prepare(Context ctx, HardwareMap basicHardwareMap, Gamepad gamepad1, Gamepad gamepad2, Telemetry telemetry) {
        checkArgument(ctx instanceof Activity, "Invalid context; it must be of an activity context type");
        bindAppContext(ctx);
        bindHardwareMap(checkNotNull(basicHardwareMap));
        this.gamepad1 = checkNotNull(gamepad1, "Gamepad 1 is null");
        this.gamepad2 = checkNotNull(gamepad2, "Gamepad 2 is null");
        extensibleTelemetry = new ExtensibleTelemetry(checkNotNull(telemetry, "telemetry is null"));
        layout = ((Activity) appContext()).findViewById(controllerBindings().integers().get(DataBinder.RC_VIEW));
        cameraViewParent = ((Activity) appContext()).findViewById(controllerBindings().integers().get(DataBinder.CAMERA_VIEW));

        eventManager.run();
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
            throw new NullPointerException();
        }
    }

    private List<Runnable> findAsyncMethods(Class klazz, HashMap<String, Runnable> runnables) {
        if (runnables == null) {
            runnables = new HashMap<>();
        }

        if (klazz.equals(Object.class)) {
            return new LinkedList<>(runnables.values());
        }

        for (final Method method : klazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Async.class)) {
                Log.i("ASYNC_RUNNER", "Discovered async " + method.getName());
                if (!runnables.containsKey(method.getName())) {
                    method.setAccessible(true);
                    runnables.put(method.getName(), new Runnable() {
                        @Override
                        public void run() {
                            ///asyncsRunning.add(Thread.currentThread());
                            try {
                                final String name = method.getDeclaringClass() + "#" + method.getName();
                                Thread.currentThread().setName(name);
                                Log.i("ASYNC_RUNNER", "Begin " + name);
                                method.invoke(RobotContext.this);
                                Log.i("ASYNC_RUNNER", "End " + method.getDeclaringClass() + "#" + method.getName());
                            } catch (IllegalAccessException e) {
                                Log.e("ASYNC_RUNNER", "Failed to start method", e);
                            } catch (InvocationTargetException e) {
                                Throwables.propagate(e.getTargetException());
                            } catch (Exception e) {
                                Log.e("ASYNC_RUNNER", "An error occurred while executing" +
                                        " async runner \"" + Thread.currentThread().getName() + "\".", e);
                            }
                            //asyncsRunning.remove(Thread.currentThread());
                        }
                    });
                }
            }
        }

        return findAsyncMethods(klazz.getSuperclass(), runnables);
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
    public void addPostData(Collection<?> datas) {
        // TODO: 12/19/2016 rebuild networking
        RobotLog.w("[xNetworking] stub!");
    }

    /**
     * Get a copy of the received data for use
     *
     * @return an <code>ImmutableList</code> that is a copy of the data received via networking
     */
    @Override
    @Deprecated
    public ImmutableList<?> getPostedData() {
        // TODO: 12/19/2016 rebuild networking
        RobotLog.w("[xNetworking] stub!");
        // return ImmutableList.copyOf(postedData);

        return ImmutableList.copyOf(Collections.EMPTY_LIST);
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

        if (asyncService.isTerminated()) {
            //for (Thread async : asyncsRunning) {
            //    async.interrupt();
            //}
            //asyncService.shutdown();
            asyncService.shutdownNow();
        }

        if (extensibleTelemetry != null) {
            extensibleTelemetry = null;
        }

        if (eventManager != null) {
            eventManager.shutdown();
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
     * Gets the entire robot controller view area
     *
     * @return a View representing the real estate of the entire activity
     *
     * @see
     */
    @Override
    @NotNull
    public View robotControllerView() {
        if (layout == null)
            throw new IllegalStateException("Prepare has not been called correctly yet.");
        return layout;
    }

    @Override
    @NotNull
    public View cameraView() {
        if (cameraViewParent == null)
            throw new IllegalStateException("Prepare has not been called correctly yet.");
        return cameraViewParent;
    }

    public List<RobotEvent> events() {
        return events;
    }

    public RobotContext registerEvent(@NotNull Object value, @NotNull Object goalValue, RobotAction... actions) {
        for (RobotAction action : actions) {
            registerEvent(new RobotEvent(value, goalValue, action));
        }

        return this;
    }

    public RobotContext registerEvent(@NotNull RobotEvent event) {
        events.add(event);
        return this;
    }
}
