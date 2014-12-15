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

    int H_MIN_1 = 92;

    int S_MIN_1 = 174;

    int V_MIN_1 = 144;

    int W_MIN_1 = 0;

    int H_MAX_1 = 120;

    int S_MAX_1 = 255;

    int V_MAX_1 = 255;

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

    private MouseMover mouseMover;

    public WebCamCaptureTest() {
        mouseMover = new MouseMover();
    }

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

        JFrame jFrame3 = new JFrame("Edge-1");
        jFrame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame3.setSize(1000, 1000);
        WebCamCaptureTest edgePlane = new WebCamCaptureTest();
        jFrame3.setContentPane(edgePlane);
        jFrame3.setVisible(true);

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
        Mat kernel = Mat.ones(3, 3, CvType.CV_8U).setTo(new Scalar(.33));
        Mat kernel2 = Mat.ones(3, 3, CvType.CV_32S);
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

                    Mat hsv = threshold1.clone();
                    //                    Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2GRAY);
                    //                    Imgproc.distanceTransform(hsv, hsv, Imgproc.CV_DIST_L2, 5);
                    //                    Imgproc.threshold(hsv, hsv, H_MIN_1, H_MAX_1,
                    // Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

                    Core.inRange(threshold1, new Scalar(H_MIN_1, S_MIN_1, V_MIN_1, W_MIN_1),
                            new Scalar(H_MAX_1, S_MAX_1, V_MAX_1, W_MAX_1), threshold1);
                    //                    Imgproc.GaussianBlur(threshold1, threshold1, new Size(5, 5), 0);


                    Imgproc.morphologyEx(threshold1, threshold1, Imgproc.MORPH_OPEN, kernel);
                    Mat morphOpen = threshold1.clone();
                    Imgproc.GaussianBlur(morphOpen, morphOpen, new Size(3, 3), 1.0);

                    Mat edge = threshold1.clone();
                    /*Imgproc.GaussianBlur(threshold1, threshold1, new Size(5, 5), 0);
                    Mat thresMat = new Mat();
                    Imgproc.threshold(threshold1, threshold1, H_MIN_1, H_MAX_1, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
                    //                    Photo.fastNlMeansDenoising(threshold1, thresMat, 5.0f, 10, 10);

*/


                    List<MatOfPoint> matOfPointList = new ArrayList<>();

                    Imgproc.findContours(
                            threshold1,
                            matOfPointList,
                            hierarchy,
                            Imgproc.RETR_EXTERNAL,
                            Imgproc.CHAIN_APPROX_SIMPLE);
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

                    List<Point> allConvexPoints = new ArrayList<>();
                    List<Point> convexPoints = new ArrayList<>();
                    List<Point> tipPoints = new ArrayList<>();
                    List<Point> realTips = new ArrayList<>();

                    List<Point> contList = maxCont.toList();
                    calculateDefectPoints(defects1, noOfDefects, allConvexPoints, convexPoints, tipPoints, contList, realTips);

                    Moments moments = Imgproc.moments(matOfPointList.get(index), true);
                    extractContureDetails(moments, cog);

                    //                    Imgproc.drawContours(frame, matOfPointList, index, new Scalar(0, 255, 0), 5);
                    Core.circle(frame, cog, 5, new Scalar(0, 255, 0), 5);

                    for (Point point : convexPoints) {
                        Core.circle(frame, point, 2, new Scalar(255, 0, 255), 3);
                    }
                    //                    Mat onlyPoints = new Mat(new Size(650, 480), CvType.CV_8UC1, new Scalar(255, 255, 255));

                    for (Point point : realTips) {
                        Core.line(frame, point, cog, new Scalar(0, 255, 255), 3);
                        Core.circle(frame, point, 2, new Scalar(255, 255, 255), 3);
                    }
                    if (cog.x > 10 && cog.y > 10) {
                        mouseMover.moveMouse((int) (cog.x / 1), (int) (cog.y / 1));

                    }
                    System.out.println(cog.x + " : " + cog.y);
                    repaint(jFrame, panel, morphOpen);
                    repaint(jFrame2, threshHoldPanel1, frame);
                    repaint(jFrame3, edgePlane, hsv);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void calculateDefectPoints(MatOfInt4 defects1,
                                       int noOfDefects,
                                       List<Point> allConvexPoints,
                                       List<Point> convexPoints,
                                       List<Point> tips,
                                       List<Point> contList,
                                       List<Point> realTips) {
        double maxDepth = 0.0;
        for (int i = 0; i < noOfDefects; i++) {
            Mat row = defects1.row(i);
            double furthest = row.get(0, 0)[2];
            double start = row.get(0, 0)[1];
            double depth = row.get(0, 0)[3];
            if (maxDepth < depth) {
                maxDepth = depth;
            }
            allConvexPoints.add(contList.get((int) start));
            tips.add(contList.get((int) furthest));
        }

        for (int i = 0; i < noOfDefects; i++) {
            Mat row = defects1.row(i);
            double start = row.get(0, 0)[2];
            double depth = row.get(0, 0)[3];
            if (maxDepth / depth < 2) {
                Point point1 = contList.get((int) start);
                convexPoints.add(point1);
            }

        }
        findTips(defects1, noOfDefects, allConvexPoints, maxDepth, tips, realTips);
    }

    private void findTips(MatOfInt4 defects1,
                          int noOfDefects,
                          List<Point> allConvexPoints,
                          double maxDepth,
                          List<Point> tips,
                          List<Point> realTips) {

        for (int i = 0; i < noOfDefects; i++) {
            int pdx = (i == 0) ? (noOfDefects - 1) : (i - 1);
            int sdx = (i == noOfDefects - 1) ? 0 : (i + 1);
            int i1 = angleBetween(tips.get(i), allConvexPoints.get(sdx), allConvexPoints.get(pdx));
            if (i1 < 60) {
                break;
            }
            Mat row = defects1.row(i);
            double start = row.get(0, 0)[1];
            double depth = row.get(0, 0)[3];
            if (maxDepth / depth < 2) {
                realTips.add(allConvexPoints.get(pdx));
                realTips.add(allConvexPoints.get(sdx));
            }
        }

    }

    private int angleBetween(Point tip, Point next, Point prev) {
        return Math.abs((int) Math.round(
                Math.toDegrees(
                        Math.atan2(next.x - tip.x, next.y - tip.y) -
                                Math.atan2(prev.x - tip.x, prev.y - tip.y))));
    }

    private void extractContureDetails(Moments moments, Point cog) {
        double m00 = moments.get_m00();
        double m10 = moments.get_m10();
        double m01 = moments.get_m01();

        if (m00 != 0) {
            int xCenter = (int) Math.round(m10 / m00);
            int yCenter = (int) Math.round(m01 / m00);
            cog.x = xCenter;
            cog.y = yCenter;
        }
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
