package org.ftccommunity.ftcxtensible.sensors.camera;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.lang.ref.SoftReference;

public interface CameraImageCallback {
    /**
     * Callback for image processing code, this gets called every preview data capture.
     *
     * @param orig a soft reference to the original Bitmap
     * @return null if you don't need to add an image back to the queue, otherwise want the Bitmap
     * you return will be put in the queue
     */
    @Nullable
    Bitmap processImage(SoftReference<Bitmap> orig);
}
