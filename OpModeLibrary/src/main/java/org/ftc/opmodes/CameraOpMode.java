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

package org.ftc.opmodes;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Environment;

import org.ftccommunity.ftcxtensible.hardware.camera.CameraImageCallback;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.LinkedList;

@SuppressWarnings("deprecation")
@Autonomous
public class CameraOpMode extends ExtensibleOpMode {
    int red = 0;
    int blue = 0;
    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        Bitmap image = ctx.cameraManager().getNextImage();
        if (image != null) {
            ctx.log().i(TAG, image.toString());
        }

        telemetry().data(TAG, "hello red: " + red / 255 + "%");
    }

    @Override
    public void init(final RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.cameraManager().bindCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK);
        ctx.cameraManager().prepareForCapture();
        ctx.cameraManager().getPreviewCallback().setDelay(500);

        CameraImageCallback cb = new MyCameraImageCallback(ctx);
        ctx.cameraManager().setImageProcessingCallback(cb);
        ctx.cameraManager().getNextImage();
    }

    @Override
    public void init_loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        hardwareMap().get("test");
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
    public int onFailure(RobotContext ctx, RobotStatus.Type eventType, Object event, Object in) {
        return -1;
    }

    private class MyCameraImageCallback implements CameraImageCallback {
        private final RobotContext ctx;
        private long time;
        private byte[] storage;

        public MyCameraImageCallback(RobotContext ctx) {
            this.ctx = ctx;
            time = System.nanoTime();
        }

        @Nullable
        @Override
        public Bitmap processImage(SoftReference<Bitmap> orig) {
            try {
                int[] pixels = new int[orig.get().getHeight() * orig.get().getWidth()];

                if (time + 10 * 1E9 < System.nanoTime()) {
                    time = System.nanoTime();

                    File folder = new File(Environment.getExternalStorageDirectory() + "/robot/imgs/");
                    //noinspection ResultOfMethodCallIgnored
                    folder.mkdirs();
                    String path = System.currentTimeMillis() + ".png";
                    File imageFile = new File(folder, path);
                    FileOutputStream file = new FileOutputStream(imageFile);
                    orig.get().compress(Bitmap.CompressFormat.PNG, 100, file);
                    ctx.log().i(TAG, "Saving updateGamepads data picture: " + path);
                }

                orig.get().getPixels(pixels, 0, orig.get().getWidth(), 0, 0, orig.get().getWidth(), orig.get().getHeight());

                int sum = 0;
                for (int pixel : pixels) {
                    sum += pixel;
                }

                int averageColor = sum / pixels.length;
                telemetry().data(TAG, "Average red color: " + Color.red(averageColor));
                telemetry().data(TAG, "Average blue color: " + Color.blue(averageColor));
                red = Color.red(averageColor);
                blue = Color.blue(averageColor);
                return null;
            } catch (NullPointerException ex) {
                ctx.log().i(TAG, ex.getLocalizedMessage());
                return null;
            } catch (IOException e) {
                ctx.log().e(TAG, e.getLocalizedMessage());
            }

            return null;
        }
    }
}
