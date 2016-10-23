package org.ftccommunity.ftcxtensible.cv;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.jetbrains.annotations.NotNull;

/**
 * Created by David on 9/25/2016.
 */
public class Vuforia {
    @NonNull
    public static VuforiaLocalizer build(@NotNull String apiKey, @NotNull VuforiaLocalizer.CameraDirection direction, @IdRes int viewId, boolean extendingTracking) {
        final VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(viewId);
        parameters.cameraDirection = direction;
        parameters.vuforiaLicenseKey = apiKey;
        parameters.useExtendedTracking = extendingTracking;
        return ClassFactory.createVuforiaLocalizer(parameters);
    }

    public static VuforiaLocalizerBuilder createBuilder(@NotNull String apiKey) {
        return new VuforiaLocalizerBuilder(apiKey);
    }

    public static class VuforiaLocalizerBuilder {
        final VuforiaLocalizer.Parameters parameters;
        private final String apiKey;

        public VuforiaLocalizerBuilder(@NotNull String apiKey) {
            this.apiKey = apiKey;
            parameters = new VuforiaLocalizer.Parameters();
        }

        public VuforiaLocalizerBuilder setCameraDirection(VuforiaLocalizer.CameraDirection cameraDirection) {
            parameters.cameraDirection = cameraDirection;
            return this;
        }

        public VuforiaLocalizerBuilder setParentViewId(@IdRes int viewId) {
            parameters.cameraMonitorViewIdParent = viewId;

            return this;
        }

        public VuforiaLocalizer build() {
            return ClassFactory.createVuforiaLocalizer(parameters);
        }


    }
}
