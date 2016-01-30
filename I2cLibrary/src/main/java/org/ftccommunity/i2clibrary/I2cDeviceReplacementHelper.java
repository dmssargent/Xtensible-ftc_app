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

package org.ftccommunity.i2clibrary;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.LegacyModule;

import java.util.Map;

/**
 * A helper object that assists in managing the creation of replacement hardware implementations for
 * I2C-based objects.
 */
public class I2cDeviceReplacementHelper<TARGET> {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    private OpMode context;
    private TARGET client;
    private TARGET target;
    private String targetName;
    private HardwareMap.DeviceMapping targetDeviceMapping;
    private I2cController controller;
    private int targetPort;
    private I2cController.I2cPortReadyCallback targetCallback;
    private boolean isArmed;

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    public I2cDeviceReplacementHelper(OpMode context, TARGET client, /* may be null */ TARGET target, I2cController controller, int targetPort) {
        this.context = context;
        this.isArmed = false;
        this.client = client;
        this.target = target;       // may be null
        this.controller = controller;
        this.targetPort = targetPort;

        this.targetName = null;
        this.targetDeviceMapping = null;
        if (this.target != null)
            findTargetNameAndMapping();

        if (controller instanceof LegacyModule) {
            this.targetCallback = MemberUtil.callbacksOfLegacyModule((LegacyModule) controller)[targetPort];
        } else if (controller instanceof DeviceInterfaceModule) {
            this.targetCallback = MemberUtil.callbacksOfDeviceInterfaceModule((DeviceInterfaceModule) controller)[targetPort];
        } else
            throw new IllegalArgumentException(String.format("unknown controller flavor: %s", controller.getClass().getSimpleName()));
    }

    //----------------------------------------------------------------------------------------------
    // Operations
    //----------------------------------------------------------------------------------------------

    boolean isArmed() {
        return this.isArmed;
    }

    void arm() {
        if (!this.isArmed) {
            // Have the existing controller stop using the callback
            this.controller.deregisterForPortReadyCallback(this.targetPort);

            // Put ourselves in the hardware map
            if (this.targetName != null) this.targetDeviceMapping.put(this.targetName, this.client);
            this.isArmed = true;
        }
    }

    void disarm() {
        if (this.isArmed) {
            this.isArmed = false;

            // Put the original guy back in the hardware map
            if (this.targetName != null) this.targetDeviceMapping.put(this.targetName, this.target);

            // Start up the original controller again
            this.controller.registerForI2cPortReadyCallback(this.targetCallback, this.targetPort);
        }
    }

    //----------------------------------------------------------------------------------------------
    // Utility
    //----------------------------------------------------------------------------------------------

    private void findTargetNameAndMapping() {
        for (HardwareMap.DeviceMapping<?> mapping : Util.deviceMappings(this.context.hardwareMap)) {
            for (Map.Entry<String, ?> pair : mapping.entrySet()) {
                if (pair.getValue() == this.target) {
                    this.targetName = pair.getKey();
                    this.targetDeviceMapping = mapping;
                    return;
                }
            }
        }
    }
}
