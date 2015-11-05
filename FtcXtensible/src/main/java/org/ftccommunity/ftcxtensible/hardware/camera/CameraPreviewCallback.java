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

package org.ftccommunity.ftcxtensible.hardware.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

import org.ftccommunity.ftcxtensible.robot.RobotContext;

import java.io.ByteArrayOutputStream;

public class CameraPreviewCallback implements Camera.PreviewCallback {
    private static final String TAG = CameraPreviewCallback.class.getSimpleName();
    private final ByteArrayOutputStream outputStream;
    private int delay = 100;
    private RobotContext context;
    private long timestamp;

    public CameraPreviewCallback(RobotContext ctx, int captureDelay) {
        context = ctx;
        setDelay(captureDelay);
        outputStream = new ByteArrayOutputStream();
    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        if (timestamp + getDelay() * 1E6 > System.nanoTime()) {
            return;
        }

        if (context.cameraManager() != null && context.cameraManager().getCamera() != null) {
            Camera.Parameters parameters = context.cameraManager().getCamera().getParameters();
            Camera.Size previewSize = parameters.getPreviewSize();
            YuvImage image = new YuvImage(data, parameters.getPreviewFormat(),
                    previewSize.width,
                    parameters.getPreviewSize().height, null);
            byte[] jpeg;
            synchronized (outputStream) {
                image.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, outputStream);
                jpeg = outputStream.toByteArray();
                outputStream.reset();
            }
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                context.cameraManager().addImage(bitmap);
                timestamp = System.nanoTime();
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }

        }
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int milliseconds) {
        this.delay = milliseconds;
    }
}

