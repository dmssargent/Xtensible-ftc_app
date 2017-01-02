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
package org.ftccommunity.ftcxtensible.interfaces;

import android.content.Context;
import android.view.View;

import com.google.common.collect.ImmutableList;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.ftccommunity.bindings.DataBinder;
import org.ftccommunity.ftcxtensible.hardware.camera.ExtensibleCameraManager;
import org.ftccommunity.ftcxtensible.networking.ServerSettings;
import org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad;
import org.ftccommunity.ftcxtensible.robot.ExtensibleHardwareMap;
import org.ftccommunity.ftcxtensible.robot.ExtensibleTelemetry;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotLogger;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Main Robot Context Interface, any class claiming to be a Robot Context compatible class should
 * inherit this interface
 *
 * @author David
 * @see RobotContext
 * @since 0.2.2
 */
public interface AbstractRobotContext {
    RobotContext enableNetworking();

    RobotContext disableNetworking();

    void bindHardwareMap(@NotNull HardwareMap map);

    void rebuildHardwareMap();

    @Deprecated
    HardwareMap legacyHardwareMap();

    RobotContext startNetworking();

    RobotContext stopNetworking();

    /**
     * Returns the Hardware Map
     *
     * @return the <code>ExtensibleHardwareMap</code> currently in use
     */
    ExtensibleHardwareMap hardwareMap();

    /**
     * Binds a new activity context to this
     *
     * @param context a non-null activity context
     * @throws IllegalStateException, IllegalArgumentException
     */
    void bindAppContext(Context context) throws IllegalArgumentException, IllegalStateException;

    //void prepare(Context ctx, HardwareMap basicHardwareMap, Gamepad gamepad1, Gamepad gamepad2);

    void prepare(Context ctx, HardwareMap basicHardwareMap, Gamepad gamepad1, Gamepad gamepad2, Telemetry telemetry);

    /**
     * Returns an App Context
     *
     * @return the Android Context
     */
    Context appContext();

    /**
     * Returns opMode reference to the first gamepad
     *
     * @return the first gamepad
     * @deprecated Please use the gamepad1 instead
     */
    @Deprecated
    Gamepad legacyGamepad1();

    /**
     * Returns opMode reference to the second gampepad
     *
     * @return the second gamepad
     * @deprecated Please use the gamepad2 instead
     */
    @Deprecated
    Gamepad legacyGamepad2();

    /**
     * The current HTTP server settings
     *
     * @return <code>ServerSettings</code>
     */
    ServerSettings serverSettings();

    /**
     * An tool for writing to multiple the same message to multiple places
     *
     * @return the current {@link RobotLogger} to use
     */
    RobotLogger log();

    /**
     * Submit a Runnable to be ran
     *
     * @param runnable Runnable to run at some time
     */
    void submitAsyncTask(Runnable runnable);

    /**
     * Run a {@code Runnable} on the main app thread
     *
     * @param runnable Runnable to run on the UI Thread.
     */
    void runOnUiThread(Runnable runnable);

    /**
     * The Robot Status Object
     *
     * @return the current status of the robot
     */
    RobotStatus status();

    /**
     * Add a collection of data received for further processing by user code
     *
     * @param datas a collection of type <code>InterfaceHttpData</code> to be added
     */
    void addPostData(Collection<?> datas);

    /**
     * Get a copy of the received data for use
     *
     * @return an <code>ImmutableList</code> that is a copy of the data received via networking
     */
    ImmutableList<?> getPostedData();

    /**
     * The telemetry object to be used
     *
     * @return the telemetry as provide by the FTC SDK
     */
    ExtensibleTelemetry telemetry();

    /**
     * The first Gamepad in the form of an ExtensibleGamepad.
     *
     * @return the first Gamepad (gamepad1) cast to an ExtensibleGamepad
     */
    ExtensibleGamepad gamepad1();

    /**
     * The second Gamepad in the form of an ExtensibleGamepad
     *
     * @return the second gamepad (gampad2) cast to an ExtensibleGamepad
     */
    ExtensibleGamepad gamepad2();

    ExtensibleCameraManager cameraManager();

    void release();

    /**
     * Gets the robot controller data bindings
     *
     * @return the robot controller data binding
     */
    @NotNull
    DataBinder controllerBindings();

    /**
     * Gets the recommended robot controller view area
     *
     * @return a View representing the real estate that Qualcomm has set aside
     */
    @NotNull
    View robotControllerView();

    @NotNull
    View cameraView();
}
