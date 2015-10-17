/*
 *
 *  * Copyright © 2015 David Sargent
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  * and associated documentation files (the “Software”), to deal in the Software without restriction,
 *  * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  * and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 *  * do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all copies or
 *  * substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 *  * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.ftccommunity.ftcxtensible.sensors.camera;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.common.collect.EvictingQueue;

import org.ftccommunity.ftcxtensible.gui.CameraPreview;
import org.ftccommunity.ftcxtensible.internal.Alpha;
import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;
import org.ftccommunity.ftcxtensible.robot.RobotContext;

import java.lang.ref.SoftReference;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Handles the camera the camera behind the scenes
 *
 * @author David Sargent
 * @since 0.2
 */
@Alpha
@NotDocumentedWell
@TargetApi(19)
public class ExtensibleCameraManager {
    private static final String TAG = "CAMERA_MGR::";

    private final EvictingQueue<SoftReference<Bitmap>> imageQueue;
    CameraPreview view;
    private Camera camera;
    private Camera.CameraInfo info;
    private int cameraId;
    private Date latestTimestamp;
    private Date prepTime;
    private RobotContext context;

    private CameraPreviewCallback previewCallback;
    private CameraImageCallback imageProcessingCallback;

    public ExtensibleCameraManager(RobotContext ctx, int captureDelay) {
        context = ctx;
        imageQueue = EvictingQueue.create(5);
        latestTimestamp = new Date();

        previewCallback = new CameraPreviewCallback(ctx, captureDelay);
    }

    /**
     * A safe way to get an instance of the Camera object.
     *
     * @param cameraRotation which side is the camera on
     */
    public ExtensibleCameraManager bindCameraInstance(final int cameraRotation) {
        try {
            cameraId = Camera.getNumberOfCameras();
            for (; getCameraId() > 0; cameraId--) {
                info = new Camera.CameraInfo();
                Camera.getCameraInfo(getCameraId() - 1, info);
                if (info.facing == cameraRotation) {
                    cameraId--;
                    break;
                }
            }
        } catch (Exception e) {
            // CameraOpMode is not available (in use or does not exist)
            Log.e(TAG, e.toString(), e);
            throw e;
        }


        return this; // returns null if camera is unavailable
    }

    public ExtensibleCameraManager setImageProcessingCallback(CameraImageCallback cb) {
        imageProcessingCallback = cb;

        return this;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Camera.CameraInfo getInfo() {
        return info;
    }

    public int getCameraId() {
        return cameraId;
    }

    public Date getLatestTimestamp() {
        return latestTimestamp;
    }

    public void stop() {
        context.runOnUiThread(new StopCamera(context));
    }

    @Nullable
    public Bitmap getNextImage() {
        try {
            return imageQueue.poll().get();
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public void prepareForCapture() {
        context.runOnUiThread(new PrepCapture(context));
    }

    private void finishPrep() {
        prepTime = new Date();
    }

    private boolean isReadyForCapture() {
        return prepTime.before(new Date(prepTime.getTime() + 1000));
    }

    public void addImage(final Bitmap jpg) {
        final SoftReference<Bitmap> softBitmap = new SoftReference<>(jpg);
        if (imageProcessingCallback != null) {
            context.submitAsyncTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap post = imageProcessingCallback.processImage(softBitmap);
                        if (post != null) {
                            imageQueue.add(new SoftReference<>(post));
                        }
                    } catch (Exception ex) {
                        Log.e("CAMERA_PROCESSING::", ex.getLocalizedMessage(), ex);
                    }
                }
            });
        } else {
            imageQueue.add(softBitmap);
        }
    }

    public CameraPreviewCallback getPreviewCallback() {
        return previewCallback;
    }

    private class PrepCapture implements Runnable {
        private final RobotContext ctx;

        public PrepCapture(RobotContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            view = new CameraPreview(ctx);

            RelativeLayout relativeLayout = new RelativeLayout(context.getAppContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(view.getLayoutParams());
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.width /= 2;
            layoutParams.height /= 2;

            relativeLayout.addView(view, layoutParams);
            ((RelativeLayout) ctx.robotControllerView()).addView(relativeLayout);

            finishPrep();
        }
    }

    private class StopCamera implements Runnable {
        private final RobotContext ctx;

        public StopCamera(RobotContext ctx) {
            checkNotNull(ctx);
            this.ctx = ctx;
        }

        @Override
        public void run() {
            try {
                ((RelativeLayout) ctx.robotControllerView()).removeView(view);
                view = null;
            } catch (Exception ex) {
                Log.wtf(TAG, ex);
            }
        }
    }
}
