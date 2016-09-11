///*
// * Copyright © 2016 David Sargent
// * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// * and associated documentation files (the "Software"), to deal in the Software without restriction,
// * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
// * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
// * the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all copies or
// * substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
// * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// */
//package org.ftc.opmodes;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.os.Environment;
//import android.util.Log;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
//import org.ftccommunity.ftcxtensible.robot.RobotContext;
//import org.ftccommunity.xtensible.xsimplify.SimpleOpMode;
//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.CameraBridgeViewBase;
//import org.opencv.android.JavaCameraView;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.Utils;
//import org.opencv.calib3d.Calib3d;
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.DMatch;
//import org.opencv.core.KeyPoint;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfDMatch;
//import org.opencv.core.MatOfKeyPoint;
//import org.opencv.core.MatOfPoint2f;
//import org.opencv.core.Point;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.features2d.DescriptorExtractor;
//import org.opencv.features2d.DescriptorMatcher;
//import org.opencv.features2d.FeatureDetector;
//import org.opencv.features2d.Features2d;
//import org.opencv.imgproc.Imgproc;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Locale;
//
///**
// * @author FTC Team 10182
// */
//@SuppressWarnings("deprecation")
//@Autonomous
//public class OpenCvORBExample extends SimpleOpMode implements CameraBridgeViewBase.CvCameraViewListener2, SeekBar.OnSeekBarChangeListener {
//    public static final int VIEW_MODE_RGBA = 0;
//    public static final int TRAIN = 8;
//    public static final int SHOW_MATCHES = 9;
//    public static final int SHOW_BOX = 10;
//    public static final int SHOW_KEYPOINTS = 11;
//    private static final String TAG = "OCVSample::Activity";
//    public static int viewMode = VIEW_MODE_RGBA;
//    String _toastMsg = "";
//    SeekBar _seekBar;
//    TextView _minDistanceTextView;
//    TextView _numMatchesTextView;
//    TextView _ransacThresholdTextView;
//    DescriptorExtractor descriptorExtractor;
//    DescriptorMatcher _matcher;
//    FeatureDetector _detector;
//    Mat _descriptors;
//    MatOfKeyPoint _keypoints;
//    Mat _descriptors2;
//    MatOfKeyPoint _keypoints2;
//    // GUI Controls
//    Mat _img1;
//    String _numMatches;
//    int _minDistance;
//    int _ransacThreshold = 3;
//    private RelativeLayout relativeLayout;
//    private CameraBridgeViewBase mOpenCvCameraView;
//    private Mat mIntermediateMat;
//
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(appContext()) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS: {
//                    Log.i(TAG, "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                }
//                break;
//                default: {
//                    super.onManagerConnected(status);
//                }
//                break;
//            }
//        }
//    };
//
//    /*
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        Log.i(TAG, "called onCreateOptionsMenu");
//        mItemPreviewRGBA = menu.add("Reset");
//        mItemShowKeypoints = menu.add("Show Key Points");
//        mItemShowMatches = menu.add("Show Matches");
//        mItemShowBox = menu.add("Show Box");
//        ((Activity) appContext()).getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
//        if (item == mItemPreviewRGBA)
//            viewMode = VIEW_MODE_RGBA;
//        else if (item == mItemTrainDetector)
//            viewMode = TRAIN;
//        else if (item == mItemShowMatches)
//            viewMode = SHOW_MATCHES;
//        else if (item == mItemShowBox)
//            viewMode = SHOW_BOX;
//        else if (item == mItemShowKeypoints)
//            viewMode = SHOW_KEYPOINTS;
//        else if (item.getItemId() == R.id.action_train)
//            viewMode = TRAIN;
//        return true;
//    }
//    */
//
//    public void onCameraViewStarted(int width, int height) {
//        mIntermediateMat = new Mat();
//    }
//
//    public void onCameraViewStopped() {
//        // Explicitly deallocate Mats
//        if (mIntermediateMat != null)
//            mIntermediateMat.release();
//
//        mIntermediateMat = null;
//    }
//
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        Mat rgba = inputFrame.rgba();
//        Size sizeRgba = rgba.size();
//
//        Mat rgbaInnerWindow;
//
//        int rows = (int) sizeRgba.height;
//        int cols = (int) sizeRgba.width;
//
//        int left = cols / 8;
//        int top = rows / 8;
//
//        int width = cols * 3 / 4;
//        int height = rows * 3 / 4;
//
//        switch (viewMode) {
//            case VIEW_MODE_RGBA:
//                break;
//
//            case TRAIN:
//                viewMode = SHOW_MATCHES;
//                return trainORBDetector(inputFrame);
//            case SHOW_MATCHES:
//            case SHOW_BOX:
//            case SHOW_KEYPOINTS:
//                try {
//                    Mat gray2 = inputFrame.gray();
//                    List<DMatch> good_matches = findMatches(inputFrame);
//                    if (good_matches == null) return gray2;
//                    if (viewMode == SHOW_BOX)
//                        return drawBox(gray2, _keypoints2, good_matches);
//                    if (viewMode == SHOW_MATCHES)
//                        return drawMatches(gray2, _keypoints2, good_matches, (double) gray2.height(), (double) gray2.width());
//                    else {
//                        Mat outputImage = new Mat();
//                        Features2d.drawKeypoints(gray2, _keypoints2, outputImage);
//                        return outputImage;
//                    }
//                } catch (Exception e) {
//                    _numMatches = "";
//                    _minDistance = -1;
//                    Log.e(TAG, e.getMessage());
//                    return rgba;
//                }
//        }
//
//        return rgba;
//    }
//
//    private List<DMatch> findMatches(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
////        Log.i(TAG, "Start match");
//        Mat gray2 = inputFrame.gray();
//        _descriptors2 = new Mat();
//        _keypoints2 = new MatOfKeyPoint();
//        if (_detector == null) {
//            showToast("Detector is null. You must re-train.");
//            return null;
//        }
//
//        _detector.detect(gray2, _keypoints2);
//        descriptorExtractor.compute(gray2, _keypoints2, _descriptors2);
//
//        MatOfDMatch matches12 = new MatOfDMatch();
//        _matcher.match(_descriptors, _descriptors2, matches12);
//        List<DMatch> matches12_list = matches12.toList();
//
//        // Cross-check. (see http://answers.opencv.org/question/15/how-to-get-good-matches-from-the-orb-feature-detection-algorithm/)
//        MatOfDMatch matches21 = new MatOfDMatch();
//        _matcher.match(_descriptors2, _descriptors, matches21);
//        List<DMatch> matches21_list = matches21.toList();
//        List<DMatch> filtered_list = new ArrayList<>();
//        for (DMatch forward : matches12_list) {
//            if (forward.trainIdx > matches21_list.size() - 1) continue;
//            DMatch backward = matches21_list.get(forward.trainIdx);
//            if (backward.trainIdx == forward.queryIdx)
//                filtered_list.add(forward);
//        }
//
//        double max_dist = 0;
//        double min_dist = 100;
//
//        //-- Quick calculation of max and min distances between keypoints
//        for (DMatch aFiltered_list1 : filtered_list) {
//            double dist = aFiltered_list1.distance;
//            if (dist < min_dist)
//                min_dist = dist;
//            if (dist > max_dist)
//                max_dist = dist;
//        }
//
//        //-- Draw only "good" matches (i.e. whose distance is less than 3*min_dist )
//        List<DMatch> good_matches_list = new ArrayList<>();
//        for (DMatch aFiltered_list : filtered_list) {
//            if (aFiltered_list.distance < 60) {
//                good_matches_list.add(aFiltered_list);
//            }
//        }
//
////        Log.i(TAG, "Found this many matches: " + good_matches_list.size());
//        _numMatches = String.format("%d/%d/%d", matches12_list.size(), filtered_list.size(), good_matches_list.size());
//        _minDistance = (int) min_dist;
//        updateTextViews();
//
//        return good_matches_list;
//    }
//
//    private Mat drawMatches(Mat gray2, MatOfKeyPoint _keypoints2, List<DMatch> matches_list, double rows, double cols) {
//        Mat outputImg = new Mat();
//
//        MatOfDMatch good_matches = new MatOfDMatch();
//        for (DMatch match : matches_list) {
//            MatOfDMatch temp = new MatOfDMatch();
//            temp.fromArray(match);
//            good_matches.push_back(temp);
//        }
//
//        Features2d.drawMatches(_img1, _keypoints, gray2, _keypoints2, good_matches, outputImg);
//        Mat resizedImg = new Mat();
//        Imgproc.resize(outputImg, resizedImg, new Size(cols, rows));
//        return resizedImg;
//    }
//
//    private Mat drawBox(Mat gray2, MatOfKeyPoint _keypoints2, List<DMatch> good_matches_list) {
//        LinkedList<Point> objList = new LinkedList<>();
//        LinkedList<Point> sceneList = new LinkedList<>();
//        List<KeyPoint> _keypoints2_List = _keypoints2.toList();
//        List<KeyPoint> keypoints_List = _keypoints.toList();
//
//        for (DMatch aGood_matches_list : good_matches_list) {
//            objList.addLast(keypoints_List.get(aGood_matches_list.queryIdx).pt);
//            sceneList.addLast(_keypoints2_List.get(aGood_matches_list.trainIdx).pt);
//        }
//
//        MatOfPoint2f obj = new MatOfPoint2f();
//        obj.fromList(objList);
//
//        MatOfPoint2f scene = new MatOfPoint2f();
//        scene.fromList(sceneList);
//
//        Mat hg = Calib3d.findHomography(obj, scene, Calib3d.RANSAC, _ransacThreshold);
//
//        Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
//        Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);
//
//        obj_corners.put(0, 0, 0, 0);
//        obj_corners.put(1, 0, _img1.cols(), 0);
//        obj_corners.put(2, 0, _img1.cols(), _img1.rows());
//        obj_corners.put(3, 0, 0, _img1.rows());
//        //obj_corners:input
//
//        Mat outputImage = new Mat();
//        Features2d.drawKeypoints(gray2, _keypoints2, outputImage);
//
//        Core.perspectiveTransform(obj_corners, scene_corners, hg);
//        int adj = 0;
//        Imgproc.line(outputImage, adjustPoint(adj, scene_corners.get(0, 0)), adjustPoint(adj, scene_corners.get(1, 0)), new Scalar(0, 255, 0), 4);
//        Imgproc.line(outputImage, adjustPoint(adj, scene_corners.get(1, 0)), adjustPoint(adj, scene_corners.get(2, 0)), new Scalar(0, 255, 0), 4);
//        Imgproc.line(outputImage, adjustPoint(adj, scene_corners.get(2, 0)), adjustPoint(adj, scene_corners.get(3, 0)), new Scalar(0, 255, 0), 4);
//        Imgproc.line(outputImage, adjustPoint(adj, scene_corners.get(3, 0)), adjustPoint(adj, scene_corners.get(0, 0)), new Scalar(0, 255, 0), 4);
//
//        Log.i(TAG, "Done matching");
//        return outputImage;
//    }
//
//    private Mat trainORBDetector(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        Mat gray1 = inputFrame.gray();
//
//        _descriptors = new Mat();
//        _keypoints = new MatOfKeyPoint();
//        _detector = FeatureDetector.create(FeatureDetector.ORB);
//
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ORBApp");
//        String fileName = mediaStorageDir.getPath() + "/orb_params2.yml";
//        _detector.write(fileName);
//        fileName = mediaStorageDir.getPath() + "/orb_params2.xml";
//        _detector.write(fileName);
//
//        String tempFileName = Utilities.writeToFile("tempFile", "%YAML:1.0\nscaleFactor: 1.1\nnLevels: 8\nfirstLevel: 0\nedgeThreshold: 31\npatchSize: 31\n");
//        _detector.read(tempFileName);
//
//        descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
//        fileName = mediaStorageDir.getPath() + "/extractor_params2.yml";
//        descriptorExtractor.write(fileName);
//        fileName = mediaStorageDir.getPath() + "/extractor_params2.xml";
//        descriptorExtractor.write(fileName);
//
//        _matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT);
//
//        _detector.detect(gray1, _keypoints, _descriptors);
//        descriptorExtractor.compute(gray1, _keypoints, _descriptors);
//
//        _img1 = gray1.clone();
//
//        Mat outputImage = new Mat();
//        Features2d.drawKeypoints(gray1, _keypoints, outputImage);
//        Log.i(TAG, "Found keypoints: " + _keypoints.toList().size());
//        Utilities.saveImg(outputImage);
//        showToast("Found keypoints.");
//        fileName = mediaStorageDir.getPath() + "/orb_params.yml";
//        _detector.write(fileName);
//        return outputImage;
//    }
//
//    private Point adjustPoint(int adj, double[] pt) {
//        pt[0] += adj;
//        return new Point(pt);
//    }
//
//    private void updateTextViews() {
//        context().runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                _numMatchesTextView.setText(String.valueOf(_numMatches));
//                _minDistanceTextView.setText(String.valueOf(_minDistance));
//                _ransacThresholdTextView.setText(String.valueOf(_ransacThreshold));
//
//            }
//        });
//    }
//
//    //method for when the progress bar is changed
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress,
//                                  boolean fromUser) {
//        _ransacThreshold = progress;
////        Log.i(TAG, "ransace t = " + _ransacThreshold);
//    }
//
//    //method for when the progress bar is first touched
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//    }
//
//    //method for when the progress bar is released
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//    }
//
//    private void showToast(String msg) {
//        _toastMsg = msg;
//        context().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(appContext(), _toastMsg, Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//    private View findViewById(int view) {
//        return ((Activity) appContext()).findViewById(view);
//    }
//
//    void finishInit() {
//        mOpenCvCameraView.setCvCameraViewListener(this);
//        _seekBar.setOnSeekBarChangeListener(this);
//    }
//
//    @Override
//    public void init(RobotContext ctx) {
//        runOnUiThread(new Runnable() {
//                          @Override
//                          public void run() {
//                              cameraManager().bindCameraInstance(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK);
//
//                              relativeLayout = new RelativeLayout(appContext());
//                              JavaCameraView cameraView = new JavaCameraView(appContext(), cameraManager().getCameraId());
//                              TextView numMatches = new TextView(appContext());
//                              TextView minValue = new TextView(appContext());
//                              TextView ransacThreshold = new TextView(appContext());
//                              LinearLayout seekbars = new LinearLayout(appContext());
//                              SeekBar ransacSeekBar = new SeekBar(appContext());
//
//                              relativeLayout.addView(cameraView);
//                              relativeLayout.addView(numMatches);
//                              relativeLayout.addView(minValue);
//                              relativeLayout.addView(ransacThreshold);
//                              relativeLayout.addView(seekbars);
//                              relativeLayout.addView(ransacSeekBar);
//                              ((RelativeLayout) robotControllerView()).addView(relativeLayout);
//
//
//                              mOpenCvCameraView = cameraView;
//                              _seekBar = ransacSeekBar;
//                              _ransacThresholdTextView = ransacThreshold;
//                              _numMatchesTextView = numMatches;
//                              _minDistanceTextView = minValue;
//
//                              finishInit();
//                          }
//                      }
//        );
//
//
//
//    }
//
//    @Override
//    public void loop(RobotContext ctx) {
//        if (gamepad1().isAPressed()) {
//            viewMode = VIEW_MODE_RGBA;
//        } else if (gamepad1().isBPressed()) {
//            viewMode = TRAIN;
//        } else if (gamepad1().isYPressed()) {
//            viewMode = SHOW_MATCHES;
//        } else if (gamepad1().isXPressed()) {
//            viewMode = SHOW_BOX;
//        } else if (gamepad1().getDpad().isDownPressed()) {
//            viewMode = SHOW_KEYPOINTS;
//        } else if (gamepad1().getDpad().isUpPressed()) {
//            viewMode = TRAIN;
//        }
//
//        telemetry().data(this.getClass().getSimpleName(), viewMode);
//    }
//
//    @Override
//    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {
//        if (mOpenCvCameraView != null) {
//            mOpenCvCameraView.disableView();
//        }
//
//        if (relativeLayout != null) {
//            ((RelativeLayout) robotControllerView()).removeView(relativeLayout);
//        }
//
//    }
//
//
//}
//
//class Utilities {
//    public static final int MEDIA_TYPE_IMAGE = 1;
//    public static final int MEDIA_TYPE_VIDEO = 2;
//    private static final String TAG = "ORBDetector::Utilities";
//
//    public static String writeToFile(String fileNameRoot, String data) {
//        try {
//            File mediaStorageDir = getStorageDirectory();
//            File outputFile = File.createTempFile(fileNameRoot, ".yml", mediaStorageDir);
//            FileOutputStream stream = new FileOutputStream(outputFile);
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stream);
//            outputStreamWriter.write(data);
//            outputStreamWriter.close();
//            stream.close();
//            String fileName = outputFile.getAbsolutePath();
//            Log.i(TAG, fileName);
//            return fileName;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    public static void saveImg(Mat outputImage) {
//        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
//        if (pictureFile == null) {
//            Log.d(TAG, "Error creating media file, check storage permissions: ");
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(pictureFile);
//            Bitmap m_bmp = Bitmap.createBitmap(outputImage.width(), outputImage.height(),
//                    Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(outputImage, m_bmp);
//            m_bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.flush();
//            fos.close();
//            Log.d(TAG, "Saved image as: " + pictureFile.getName());
//        } catch (FileNotFoundException e) {
//            Log.d(TAG, "File not found: " + e.getMessage());
//        } catch (IOException e) {
//            Log.d(TAG, "Error accessing file: " + e.getMessage());
//        }
//    }
//
//    private static File getOutputMediaFile(int type) {
//        File mediaStorageDir = getStorageDirectory();
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
//        File mediaFile;
//        if (type == MEDIA_TYPE_IMAGE) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_" + timeStamp + ".jpg");
//        } else if (type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "VID_" + timeStamp + ".mp4");
//        } else {
//            return null;
//        }
//
//        return mediaFile;
//    }
//
//    public static File getStorageDirectory() {
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "ORBApp");
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("ORBApp", "failed to create directory");
//                return null;
//            }
//        }
//        return mediaStorageDir;
//    }
//}