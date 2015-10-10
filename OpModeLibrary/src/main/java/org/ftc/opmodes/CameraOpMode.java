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

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.annotation.Nullable;

import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;
import org.ftccommunity.ftcxtensible.sensors.camera.CameraImageCallback;

import java.lang.ref.SoftReference;
import java.util.LinkedList;

import boofcv.alg.descriptor.UtilFeature;
import boofcv.alg.feature.color.GHistogramFeatureOps;
import boofcv.alg.feature.color.Histogram_F64;
import boofcv.android.ConvertBitmap;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.MultiSpectral;

@Autonomous
public class CameraOpMode extends ExtensibleOpMode {

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        Bitmap image = ctx.cameraManager().getNextImage();
        if (image != null) {
            ctx.log().i(TAG, image.toString());
        }

        telemetry().data(TAG, "hello");
    }

    @Override
    public void init(final RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.cameraManager().bindCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK);
        ctx.cameraManager().prepareForCapture();

        CameraImageCallback cb = new CameraImageCallback() {
            private byte[] storage;

            @Nullable
            @Override
            public Bitmap processImage(Bitmap orig) {
                try {
                    if (storage == null) {
                        storage = ConvertBitmap.declareStorage(orig, null);
                    }

                    // Functions are also provided for multi-spectral images
                    MultiSpectral<ImageFloat32> color = ConvertBitmap.bitmapToMS(orig, null, ImageFloat32.class, storage);

                    int[] lengths = new int[color.getNumBands()];
                    for (int i = 0; i < lengths.length; i++) {
                        lengths[i] = 10;
                    }

                    // The number of bins is an important parameter.  Try adjusting it
                    SoftReference<Histogram_F64> histogram = new SoftReference<>(new Histogram_F64(lengths));

                    for (int i = 0; i < lengths.length; i++) {
                        histogram.get().setRange(i, 0, 255);
                    }

                    GHistogramFeatureOps.histogram(color, histogram.get());

                    UtilFeature.normalizeL2(histogram.get()); // normalize so that image size doesn't matter

                    return null;
                } catch (NullPointerException ex) {
                    ctx.log().i(TAG, ex.getLocalizedMessage());
                    return null;
                }
            }
        };
        ctx.cameraManager().setImageProcessingCallback(cb);
    }

    @Override
    public void init_loop(RobotContext ctx, LinkedList<Object> out) throws Exception {

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
}
