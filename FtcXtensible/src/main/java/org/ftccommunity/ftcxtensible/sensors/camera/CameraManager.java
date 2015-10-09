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

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.google.common.base.Throwables;
import com.google.common.collect.EvictingQueue;

import org.ftccommunity.ftcxtensible.gui.CameraPreview;
import org.ftccommunity.ftcxtensible.gui.CameraPreview2;
import org.ftccommunity.ftcxtensible.internal.Alpha;
import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;
import org.ftccommunity.ftcxtensible.robot.RobotContext;

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
public class CameraManager {
    private static final String TAG = "CAMERA_MGR::";

    private final EvictingQueue<Bitmap> imageQueue;
    CameraPreview view;
    private Camera camera;
    private Camera.CameraInfo info;
    private int cameraId;
    private Date latestTimestamp;
    private Date prepTime;
    private RobotContext context;

    public CameraManager(RobotContext ctx) {
        context = ctx;
        imageQueue = EvictingQueue.create(5);
        latestTimestamp = new Date();
    }

    /**
     * Check if this device has a camera
     */
    public static boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * A safe way to get an instance of the Camera object.
     *
     * @param cameraRotation which side is the camera on
     */
    public CameraManager bindCameraInstance(int cameraRotation) {
        try {
            cameraId = Camera.getNumberOfCameras();
            for (; getCameraId() > 0; cameraId = getCameraId() - 1) {
                info = new Camera.CameraInfo();
                Camera.getCameraInfo(getCameraId() - 1, info);
                if (info.facing == cameraRotation) {
                    break;
                }
            }

            camera = Camera.open(getCameraId() - 1);
        } catch (Exception e) {
            // CameraOpMode is not available (in use or does not exist)
            Log.e(TAG, e.toString(), e);
            throw e;
        }
        return this; // returns null if camera is unavailable
    }

    public Camera getCamera() {
        return camera;
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
        context.runOnUiThread(new StopCamera(context, this));
    }

    public Bitmap getNextImage() {
        return imageQueue.poll();
    }

    public void prepareForCapture(final RobotContext ctx) {
        ctx.runOnUiThread(new PrepCapture(ctx, this));
    }

    private void finishPrep() {
        prepTime = new Date();
    }

    private boolean isReadyForCapture() {
        return prepTime.before(new Date(prepTime.getTime() + 1000));
    }

    public void takePicture() {
        if (!isReadyForCapture()) {
            throw new IllegalStateException("Please wait one second after prep is called");
        }

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            if (data != null) {
                                byte[] imageBytes = new byte[data.length];
                                System.arraycopy(data, 0, imageBytes, 0, imageBytes.length);
                                imageQueue.add(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                                latestTimestamp = new Date();

                                camera.startPreview();
                            }
                        }
                    });
                } catch (Exception ex) {
                    Log.e(TAG, "An error occurred getting the photo." + ex.getLocalizedMessage());
                    Log.i(TAG, Throwables.getStackTraceAsString(ex));
                }
            }
        });
    }

    public void addImage(Bitmap jpg) {
        imageQueue.add(jpg);
        latestTimestamp = new Date();
    }

    private class PrepCapture implements Runnable {
        private final RobotContext ctx;
        private final CameraManager manager;

        public PrepCapture(RobotContext ctx, CameraManager mgr) {
            this.ctx = ctx;
            this.manager = mgr;
        }

        @Override
        public void run() {
            Rect display = new Rect();
            ((Activity) context.getAppContext()).getWindowManager().getDefaultDisplay().getRectSize(display);

            ViewGroup.LayoutParams params = new AbsoluteLayout.LayoutParams(
                    240, 240, display.centerX() - 240 / 2, display.centerY() - 240 / 2);
            view = new CameraPreview(ctx, manager);

            ((Activity) ctx.getAppContext()).addContentView(view, params);

                finishPrep();
        }
    }

    private class StopCamera implements Runnable {
        private final RobotContext ctx;
        private final CameraManager manager;

        public StopCamera(RobotContext ctx, CameraManager mgr) {
            checkNotNull(ctx);
            checkNotNull(mgr);

            this.ctx = ctx;
            this.manager = mgr;
        }

        @Override
        public void run() {
            try {
                if (view != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }

                if (camera != null) {
                    camera.release();
                    camera = null;
                }
            } catch (Exception ex) {
                Log.wtf(TAG, ex);
            }
        }
    }

}
