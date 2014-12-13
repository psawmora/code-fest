package code.fest;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: prabath
 */
public class WebCamCaptureTest extends JPanel {

    private BufferedImage bufferedImage;

    int H_MIN_1 = 18;

    int S_MIN_1 = 64;

    int V_MIN_1 = 1;

    int W_MIN_1 = 0;

    int H_MAX_1 = 23;

    int S_MAX_1 = 229;

    int V_MAX_1 = 251;

    int W_MAX_1 = 0;

 /*   int H_MIN_1 = 0;

    int S_MIN_1 = 0;

    int V_MIN_1 = 0;

    int W_MIN_1 = 0;

    int H_MAX_1 = 104;

    int S_MAX_1 = 82;

    int V_MAX_1 = 67;

    int W_MAX_1 = 0;*/

    /////////////////////////////

    int H_MIN_2 = 175;

    int S_MIN_2 = 50;

    int V_MIN_2 = 50;

    int W_MIN_2 = 0;

    int H_MAX_2 = 179;

    int S_MAX_2 = 255;

    int V_MAX_2 = 255;

    int W_MAX_2 = 0;

    int noOfRuns = 0;

    AtomicBoolean isProceed = new AtomicBoolean(true);

    public void startCapturing() {
        JFrame jFrame = new JFrame("Video");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(1000, 1000);
        WebCamCaptureTest panel = this;
        jFrame.setContentPane(panel);
        jFrame.setVisible(true);

        JFrame jFrame2 = new JFrame("Threshold-1");
        jFrame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame2.setSize(1000, 1000);
        WebCamCaptureTest threshHoldPanel1 = new WebCamCaptureTest();
        jFrame2.setContentPane(threshHoldPanel1);
        jFrame2.setVisible(true);

        addSliders("H_MIN_1", panel, H_MIN_1);
        addSliders("S_MIN_1", panel, S_MIN_1);
        addSliders("V_MIN_1", panel, V_MIN_1);
        //        addSliders("W_MIN_1", panel, W_MIN_1);
        addSliders("H_MAX_1", panel, H_MAX_1);
        addSliders("S_MAX_1", panel, S_MAX_1);
        addSliders("V_MAX_1", panel, V_MAX_1);
        //        addSliders("W_MAX_1", panel, W_MAX_1);

        /*addSliders("H_MIN_2", panel, H_MIN_2);
        addSliders("S_MIN_2", panel, S_MIN_2);
        addSliders("V_MIN_2", panel, V_MIN_2);
        addSliders("W_MIN_2", panel, W_MIN_2);
        addSliders("H_MAX_2", panel, H_MAX_2);
        addSliders("S_MAX_2", panel, S_MAX_2);
        addSliders("V_MAX_2", panel, V_MAX_2);
        addSliders("W_MAX_2", panel, W_MAX_2);*/
        VideoCapture capture = new VideoCapture(0);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }
        if (!capture.isOpened()) {
            System.out.println("WebCam capture is not success.");
            return;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        Mat frame = new Mat();
        Mat kernel = Mat.ones(3, 3, CvType.CV_32S);
        Mat threshold1 = new Mat();
        Mat hierarchy = new Mat();
        Point cog = new Point();
        List<Point> fingerPoints = new ArrayList<>();
        while (isProceed.get()) {
            //            isProceed.set(false);
            try {

                capture.read(frame);
                if (!frame.empty()) {
                    System.out.println(H_MIN_1 + " " + H_MAX_1 + " | " + S_MIN_1 + " " + S_MAX_1 + " | " + V_MIN_1 + " " +
                            V_MAX_1 + "");

                    Imgproc.cvtColor(frame, threshold1, Imgproc.COLOR_BGR2HSV);
                    Core.inRange(threshold1, new Scalar(H_MIN_1, S_MIN_1, V_MIN_1, W_MIN_1),
                            new Scalar(H_MAX_1, S_MAX_1, V_MAX_1, W_MAX_1), threshold1);
                    //                    Imgproc.GaussianBlur(threshold1, threshold1, new Size(5, 5), 0);

                    Imgproc.morphologyEx(threshold1, threshold1, Imgproc.MORPH_OPEN, kernel);

                    /*Imgproc.GaussianBlur(threshold1, threshold1, new Size(5, 5), 0);
                    Mat thresMat = new Mat();
                    Imgproc.threshold(threshold1, threshold1, H_MIN_1, H_MAX_1, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
                    //                    Photo.fastNlMeansDenoising(threshold1, thresMat, 5.0f, 10, 10);

*/
                    Mat outFinal = threshold1;


                    List<MatOfPoint> matOfPointList = new ArrayList<>();

                    Imgproc.findContours(threshold1, matOfPointList, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
                    double max = 0.0;
                    int index = 0;
                    for (int i = 0; i < matOfPointList.size(); i++) {
                        MatOfPoint matOfPoint = matOfPointList.get(i);
                        double area = Imgproc.contourArea(matOfPoint);
                        if (area > max) {
                            max = area;
                            index = i;
                        }
                    }

                    MatOfPoint maxCont = matOfPointList.get(index);
                    MatOfInt hull = new MatOfInt();
                    Imgproc.convexHull(maxCont, hull);
                    MatOfInt4 defects1 = new MatOfInt4();
                    Imgproc.convexityDefects(maxCont, hull, defects1);
                    int noOfDefects = defects1.rows();
                    List<Point> tips = new ArrayList<>();
                    List<Point> contList = maxCont.toList();
                    calculateDefectPoints(defects1, noOfDefects, tips, contList);


                    List<MatOfPoint> hullList = new ArrayList<>();

                    Moments moments = Imgproc.moments(matOfPointList.get(index), true);
                    extractContureDetails(moments, cog);

                    Imgproc.drawContours(frame, matOfPointList, index, new Scalar(0, 255, 0), 5);
                    Core.circle(frame, cog, 5, new Scalar(0, 255, 0), 5);
                    for(Point point : tips){
                        Core.line(frame,point,cog,new Scalar(0, 255, 10),4);
                    }

                   /* Mat hsv = new Mat();

                    Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2HSV);
                    Mat threshold = new Mat();
                    Mat threshold1 = new Mat();
                    Mat threshold2 = new Mat();

                    Core.inRange(hsv, new Scalar(H_MIN_1, S_MIN_1, V_MIN_1, W_MIN_1),
                            new Scalar(H_MAX_1, S_MAX_1, V_MAX_1, W_MAX_1), threshold1);

                    Core.inRange(hsv, new Scalar(H_MIN_2, S_MIN_2, V_MIN_2, W_MIN_2),
                            new Scalar(H_MAX_2, S_MAX_2, V_MAX_2, W_MAX_2), threshold2);

                    Core.bitwise_or(threshold1, threshold2, threshold);

                    Imgproc.GaussianBlur(threshold1, threshold1, new Size(5, 5), 0);

*//*
                    Mat circles = new Mat();
                    Imgproc.HoughCircles(threshold1, circles, Imgproc.CV_HOUGH_GRADIENT, 2,
                            threshold1.height() / 4, 500, 50, 0, 0);
*//*


                    List<Mat> matList = new ArrayList<>();
                    Core.split(hsv, matList);
                    Mat s = matList.get(0);
                    Mat v = matList.get(1);


                    Mat outFinal = threshold1;


                    Rect rect = new Rect(200, 200, 100, 100);
                    Rect[] rects = new Rect[]{rect};
                    CamShifting camShifting = new CamShifting();
                    camShifting.create_tracked_object(frame, rects, camShifting);
                    RotatedRect rotatedRect = camShifting.camshift_track_face(frame, rects, camShifting);

                    Mat bgr = camShifting.bgr;
                    System.out.println(rotatedRect.center.x + " : " + rotatedRect.center.y);

                    Core.circle(bgr, rotatedRect.center, 20, new Scalar(5));*/

                    repaint(jFrame, panel, outFinal);
                    repaint(jFrame2, threshHoldPanel1, frame);
                    //                    readNewParams(bufferedReader);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void calculateDefectPoints(MatOfInt4 defects1, int noOfDefects, List<Point> tips, List<Point> contList) {
        for (int i = 0; i < noOfDefects; i++) {
            Mat row = defects1.row(i);
//            double start = row.get(0, 0)[0];
            double start = row.get(0, 0)[2];

            Point point1 = contList.get((int) start);
            tips.add(point1);
        }
    }

    private void extractContureDetails(Moments moments, Point cog) {
        double m00 = moments.get_m00();
        double m10 = moments.get_m10();
        double m01 = moments.get_m01();

        if (m00 != 0) {   // calculate center
            int xCenter = (int) Math.round(m10 / m00);
            int yCenter = (int) Math.round(m01 / m00);
            cog.x = xCenter;
            cog.y = yCenter;
        }

        double m11 = moments.get_m11();
        double m20 = moments.get_m20();
        double m02 = moments.get_m02();

    }

    private void test2() {
/*
        System.out.println(H_MIN_1 + " " + H_MAX_1 + " | " + S_MIN_1 + " " + S_MAX_1 + " | " + V_MIN_1 + " " +
                V_MAX_1 + "");
        Mat threshold1 = new Mat();
        Imgproc.cvtColor(frame, threshold1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(threshold1, threshold1, new Size(5, 5), 0);
        Mat thresMat = new Mat();
*/
/*
        Core.inRange(threshold1, new Scalar(H_MIN_1, S_MIN_1, V_MIN_1, W_MIN_1),
                                    new Scalar(H_MAX_1, S_MAX_1, V_MAX_1, W_MAX_1), threshold1);
*//*

        Imgproc.threshold(threshold1, threshold1, H_MIN_1, H_MAX_1, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
//                    Photo.fastNlMeansDenoising(threshold1, thresMat, 5.0f, 10, 10);


        Mat outFinal = threshold1;
        Mat heireachy = new Mat();

        List<MatOfPoint> matOfPointList = new ArrayList<>();

        Imgproc.findContours(threshold1, matOfPointList, heireachy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        double max = 0.0;
        int index = 0;
        for (int i = 0; i < matOfPointList.size(); i++) {
            MatOfPoint matOfPoint = matOfPointList.get(i);
            double area = Imgproc.contourArea(matOfPoint);
            if (area > max) {
                max = area;
                index = i;
            }
        }
        MatOfPoint maxCont = matOfPointList.get(2);
        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(maxCont, hull);
        Imgproc.drawContours(frame, matOfPointList, 1, new Scalar(0, 255, 0), 5);

       */
/* Mat hsv = new Mat();

        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2HSV);
        Mat threshold = new Mat();
        Mat threshold1 = new Mat();
        Mat threshold2 = new Mat();

        Core.inRange(hsv, new Scalar(H_MIN_1, S_MIN_1, V_MIN_1, W_MIN_1),
                new Scalar(H_MAX_1, S_MAX_1, V_MAX_1, W_MAX_1), threshold1);

        Core.inRange(hsv, new Scalar(H_MIN_2, S_MIN_2, V_MIN_2, W_MIN_2),
                new Scalar(H_MAX_2, S_MAX_2, V_MAX_2, W_MAX_2), threshold2);

        Core.bitwise_or(threshold1, threshold2, threshold);

        Imgproc.GaussianBlur(threshold1, threshold1, new Size(5, 5), 0);

*//*
*/
/*
        Mat circles = new Mat();
        Imgproc.HoughCircles(threshold1, circles, Imgproc.CV_HOUGH_GRADIENT, 2,
                threshold1.height() / 4, 500, 50, 0, 0);
*//*
*/
/*


        List<Mat> matList = new ArrayList<>();
        Core.split(hsv, matList);
        Mat s = matList.get(0);
        Mat v = matList.get(1);


        Mat outFinal = threshold1;


        Rect rect = new Rect(200, 200, 100, 100);
        Rect[] rects = new Rect[]{rect};
        CamShifting camShifting = new CamShifting();
        camShifting.create_tracked_object(frame, rects, camShifting);
        RotatedRect rotatedRect = camShifting.camshift_track_face(frame, rects, camShifting);

        Mat bgr = camShifting.bgr;
        System.out.println(rotatedRect.center.x + " : " + rotatedRect.center.y);

        Core.circle(bgr, rotatedRect.center, 20, new Scalar(5));*//*


        repaint(jFrame, panel, outFinal);
        repaint(jFrame2, threshHoldPanel1, frame);
        //                    readNewParams(bufferedReader);
*/


    }


    private void repaint(JFrame jFrame, WebCamCaptureTest panel, Mat outFinal) throws IOException {
        MatOfByte frameBuffer = new MatOfByte();
        Highgui.imencode(".jpg", outFinal, frameBuffer);
        byte[] imgBytes = frameBuffer.toArray();
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imgBytes));
        jFrame.setSize(outFinal.width() + 40, outFinal.height() + 40);
        panel.setBufferedImage(bufferedImage);
        panel.repaint();
    }


    private void addSliders(String name, WebCamCaptureTest panel, int initVal) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 255, initVal);
        slider.setName(name);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                String name = slider.getName();
                if (!slider.getValueIsAdjusting()) {
                    int value = slider.getValue();
                    switch (name) {
                        case "H_MIN_1":
                            H_MIN_1 = value;
                            break;
                        case "H_MAX_1":
                            H_MAX_1 = value;
                            break;
                        case "S_MIN_1":
                            S_MIN_1 = value;
                            break;
                        case "S_MAX_1":
                            S_MAX_1 = value;
                            break;
                        case "V_MIN_1":
                            V_MIN_1 = value;
                            break;
                        case "V_MAX_1":
                            V_MAX_1 = value;
                            break;
                        case "W_MIN_1":
                            V_MIN_1 = value;
                            break;
                        case "W_MAX_1":
                            V_MAX_1 = value;
                            break;

                        case "H_MIN_2":
                            H_MIN_2 = value;
                            break;
                        case "H_MAX_2":
                            H_MAX_2 = value;
                            break;
                        case "S_MIN_2":
                            S_MIN_2 = value;
                            break;
                        case "S_MAX_2":
                            S_MAX_2 = value;
                            break;
                        case "V_MIN_2":
                            V_MIN_2 = value;
                            break;
                        case "V_MAX_2":
                            V_MAX_2 = value;
                            break;
                        case "W_MIN_2":
                            V_MIN_2 = value;
                            break;
                        case "W_MAX_2":
                            V_MAX_2 = value;
                            break;
                    }
                }
            }
        });
        slider.setEnabled(true);
        panel.add(slider);
    }

    @Override
    protected void paintComponent(Graphics g) {
        BufferedImage image = getBufferedImage();
        if (image == null) {
            return;
        }
        g.drawImage(image, 10, 10, image.getWidth(), image.getHeight(), this);
    }

    private static class SliderListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }
}
