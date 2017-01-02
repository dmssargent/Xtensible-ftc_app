//package org.firstinspires.ftc.teamcode.test;
//
//import android.util.Log;
//
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.util.RobotLog;
//
//import org.ftccommunity.ftcxtensible.cv.OpenCV;
//import org.ftccommunity.ftcxtensible.robot.RobotContext;
//import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfKeyPoint;
//import org.opencv.features2d.DescriptorExtractor;
//import org.opencv.features2d.FeatureDetector;
//import org.opencv.imgcodecs.Imgcodecs;
//
//@Autonomous
//public class OpenCvTest extends SimpleOpMode {
//    @Override
//    public void init(RobotContext ctx) throws Exception {
//        if (OpenCV.init()) {
//            RobotLog.i("Open CV is loaded");
//        } else {
//            RobotLog.e("OpenCV failed to load");
//        }
//
//        surf();
//
//    }
//
//    @Override
//    public void loop(RobotContext ctx) throws Exception {
//
//    }
//
//    public void surf() {
//
///**/
//        FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);
//        DescriptorExtractor SurfExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
//
//        Mat img1 = Imgcodecs.imread("/mnt/sdcard/Pictures/Clutch.jpg");//one of my face
//        Mat img2 = Imgcodecs.imread("/mnt/sdcard/Pictures/images.duckduckgo.com.jpg");//one of my different face
//
//        //extract keypoints
//        MatOfKeyPoint keypoints = new MatOfKeyPoint();
//        MatOfKeyPoint logoKeypoints = new MatOfKeyPoint();
//
//        detector.detect(img1, keypoints);//this is the problem "fatal signal"
//        Log.d("LOG!", "number of query Keypoints= " + keypoints.size());
//        detector.detect(img2, logoKeypoints);
//        Log.d("LOG!", "number of logo Keypoints= " + logoKeypoints.size());
//
//
//    }
//}
