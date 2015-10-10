package org.ftccommunity.ftcxtensible.sensors.camera;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public interface CameraImageCallback {
    @Nullable
    Bitmap processImage(Bitmap orig);
}
