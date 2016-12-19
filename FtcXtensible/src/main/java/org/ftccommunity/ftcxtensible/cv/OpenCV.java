package org.ftccommunity.ftcxtensible.cv;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class OpenCV {
    private static Status instance = Status.UNLOADED;

    public static boolean init() {
        if (instance == Status.UNLOADED) {
            initBare();
        }

        if (instance == Status.LOADED_BARE) {
            initExtras();
        }

        return instance == Status.LOADED_FULL;
    }

    public static boolean initBare() {
        try {
            final Class<?> aClass = Class.forName("org.opencv.android.OpenCVLoader");
            final Method initDebug = aClass.getMethod("initDebug");
            boolean b = (boolean) initDebug.invoke(null);

            if (b) instance = Status.LOADED_BARE;
            return b;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("OpenCV is not currently available, please re-add that dependency and try again");
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("[OPENCV-initBare] Something bad happened, and I can't call the method I need to. Let someone know about this.", e);
        } catch (InvocationTargetException e) {
            final Throwable targetException = e.getTargetException();
            throw new RuntimeException(targetException.getMessage(), targetException);
        }
    }

    public static boolean initExtras() {
        if (instance == Status.UNLOADED)
            throw new IllegalStateException("Call initBare() before you call initExtras()");
        try {
            System.loadLibrary("xfeatures2d");
            instance = Status.LOADED_FULL;
        } catch (UnsatisfiedLinkError ex) {
            throw new RuntimeException("Cannot fully instantiate Open CV", ex);
        }

        return true;
    }


    enum Status {
        UNLOADED, LOADED_BARE, LOADED_FULL
    }
}
