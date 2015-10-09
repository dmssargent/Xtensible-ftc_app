package org.ftccommunity.ftcxtensible.sensors.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

import org.ftccommunity.ftcxtensible.robot.RobotContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraPreviewCallback implements Camera.PreviewCallback {
    private RobotContext context;

    public CameraPreviewCallback(RobotContext ctx) {
        context = ctx;
    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        context.submitAsyncTask(new Runnable() {
            @Override
            public void run() {
                Camera.Parameters parameters = context.cameraManager().getCamera().getParameters();
                Camera.Size previewSize = parameters.getPreviewSize();
                YuvImage image = new YuvImage(data, parameters.getPreviewFormat(),
                        previewSize.width,
                        parameters.getPreviewSize().height, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, os);

                byte[] jpeg = os.toByteArray();
                try {
                    os.close();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                    context.cameraManager().addImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
