/*
 * Copyright © 2015 David Sargent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftc.opmodes;

import android.hardware.Camera;

import org.ftccommunity.ftcxtensible.RobotContext;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;

import java.util.LinkedList;

@Autonomous
public class CameraOpMode extends ExtensibleOpMode {

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        if (getLoopCount() % 10 == 0) {
            ctx.cameraManager().takePicture();
        }

        if (getLoopCount() % 5 == 0) {
            Byte[] image = ctx.cameraManager().getImages().peek();
            ctx.log().i(TAG, Byte.toString(image[0]) + " " + Byte.toString(image[5]));
        }

        ctx.cameraManager().getImages().peek();
    }

    @Override
    public void init(final RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.cameraManager().bindCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK);
        ctx.cameraManager().prepareForCapture(ctx);
    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.cameraManager().stop();
    }

    @Override
    public void onSuccess(RobotContext ctx, Object event, Object in) {

    }

    @Override
    public int onFailure(RobotContext ctx, Type eventType, Object event, Object in) {
        return -1;
    }


}
