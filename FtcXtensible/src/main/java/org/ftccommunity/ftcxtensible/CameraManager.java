/*
 * Copyright © 2015 David Sargent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;

import com.google.common.collect.EvictingQueue;

import org.ftccommunity.ftcxtensible.gui.CameraPreview;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;

/**
 * Handles the camera the camera behind the scenes
 *
 * @author David Sargent
 * @since 0.2
 */
public class CameraManager {
    private static String TAG = "CAMERA_MGR::";
    final EvictingQueue<Byte[]> imageQueue;
    private Camera camera;
    private Camera.CameraInfo info;
    private int cameraId;
    private Date latestTimestamp;
    private RobotContext context;
    private Date prepTime;

    public CameraManager(RobotContext ctx) {
        context = ctx;
        imageQueue = EvictingQueue.create(5);
    }

    /**
     * Check if this device has a camera
     */
    public static boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputVideoFile(String name) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/FIRST",
                name);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(name, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "video_" + timeStamp + ".mp4");

        return mediaFile;
    }

    /**
     * A safe way to get an instance of the Camera object.
     *
     * @param cameraRotation which side is the camera on
     * @throws Exception when the Camera cannot be gotten
     */
    public CameraManager bindCameraInstance(int cameraRotation) throws Exception {
        try {
            cameraId = Camera.getNumberOfCameras();
            for (; getCameraId() > 0; cameraId = getCameraId() - 1) {
                info = new Camera.CameraInfo();
                Camera.getCameraInfo(getCameraId() - 1, info);
                if (info.facing == cameraRotation) {
                    break;
                }
            }

            //Camera.open(cameraId);
            camera = Camera.open(getCameraId() - 1);
            if (camera != null) {
                camera.unlock();
            }
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
        camera.release();
        camera = null;
    }

    public Queue<Byte[]> getImages() {
        return imageQueue;
    }

    public void prepareForCapture(final RobotContext ctx) {
        ctx.runOnUiThread(new PrepCapture(ctx, this));
    }

    void finishPrep() {
        prepTime = new Date();
    }

    public boolean isReadyForCapture() {
        return prepTime.before(new Date(prepTime.getTime() + 1000));
    }

    public void takePicture() throws RuntimeException {
        if (isReadyForCapture()) {
            throw new IllegalStateException("Please wait one second after prep is called");
        }

        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Byte[] bytes = new Byte[data.length];

                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = data[i];
                }


                imageQueue.add(bytes);
                latestTimestamp = new Date();
            }
        });
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
            ViewGroup.LayoutParams params = new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            CameraPreview view = new CameraPreview(ctx.getAppContext(), manager);

            ((Activity) ctx.getAppContext()).addContentView(view, params);
            finishPrep();
        }
    }
}
