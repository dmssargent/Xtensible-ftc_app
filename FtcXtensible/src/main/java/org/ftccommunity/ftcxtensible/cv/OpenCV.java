//package org.ftccommunity.ftcxtensible.cv;
//
//import org.opencv.android.OpenCVLoader;
//
//public class OpenCV {
//    private static Status instance = Status.UNLOADED;
//
//    public static boolean init() {
//        if (instance == Status.UNLOADED) {
//            initBare();
//        }
//
//        if (instance == Status.LOADED_BARE) {
//            initExtras();
//        }
//
//        return instance == Status.LOADED_FULL;
//    }
//
//    public static boolean initBare() {
//        final boolean b = OpenCVLoader.initDebug();
//        if (b) instance = Status.LOADED_BARE;
//        return b;
//    }
//
//    public static boolean initExtras() {
//        try {
//            System.loadLibrary("xfeatures2d");
//            instance = Status.LOADED_FULL;
//        } catch (UnsatisfiedLinkError ex) {
//            throw new RuntimeException("Cannot fully instantiate Open CV", ex);
//        }
//
//        return true;
//    }
//
//
//    enum Status {
//        UNLOADED, LOADED_BARE, LOADED_FULL
//    }
//}
