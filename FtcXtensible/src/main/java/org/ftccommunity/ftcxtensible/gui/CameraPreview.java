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
package org.ftccommunity.ftcxtensible.gui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.common.annotations.Beta;

import org.ftccommunity.ftcxtensible.hardware.camera.ExtensibleCameraManager;
import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;
import org.ftccommunity.ftcxtensible.robot.RobotContext;

import java.io.IOException;

/**
 * A basic Camera preview class
 *
 * @author David Sargent
 * @since 0.2.0
 */
@Beta
@NotDocumentedWell
 public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CAMERA_PREVIEW::";
    private final Context context;
    private RobotContext robotContext;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private ExtensibleCameraManager manager;


    private CameraPreview(Context ctx) {
        super(ctx);
        this.context = ctx;
    }


    /**
     * Builds a Camera Preview View, based on a Robot Context
     *
     * @param ctx Robot Context
     */
    public CameraPreview(RobotContext ctx) {
        this(ctx.appContext());
        robotContext = ctx;
        bindCameraManager(ctx.cameraManager());
    }

    /**
     * Binds a CameraManager to this preview, needed before output can occur
     *
     * @param mgr an Camera Manager of, or derived from, an ExtensibleCameraManager
     * @see ExtensibleCameraManager
     */
    public void bindCameraManager(ExtensibleCameraManager mgr) {
        manager = mgr;

        try {
            mCamera = Camera.open(mgr.getCameraId());
            mgr.setCamera(mCamera);
        } catch (Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(manager.getPreviewCallback());

            mCamera.enableShutterSound(false);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setVideoStabilization(true);
            parameters.setAutoWhiteBalanceLock(false);
            parameters.setAutoWhiteBalanceLock(true);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            parameters.setJpegQuality(70);
            mCamera.setParameters(parameters);

            // mCamera.startPreview();


        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        mCamera.setPreviewCallback(null);
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to gentleStop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // gentleStop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to gentleStop a non-existent preview
            Log.e(TAG, "An exception occurred during preview gentleStop.", e);
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        setCameraDisplayOrientation();
        // start preview with new settings

        if (robotContext.cameraManager().getPreviewCallback() != null) {
            mCamera.setPreviewCallback(robotContext.cameraManager().getPreviewCallback());
        }
        try {
            // mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private void setCameraDisplayOrientation() {
        try {
            Camera.CameraInfo info =
                    new Camera.CameraInfo();
            Camera.getCameraInfo(manager.getCameraId(), info);


            int rotation = ((Activity) context).getWindowManager().getDefaultDisplay()
                    .getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }

            mCamera.setDisplayOrientation(result);
        } catch (RuntimeException ex) {
            Log.wtf(TAG, ex);
        }
    }
}