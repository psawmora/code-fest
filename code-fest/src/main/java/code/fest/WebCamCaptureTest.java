package code.fest;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: prabath
 */
public class WebCamCaptureTest extends JPanel {

    private BufferedImage bufferedImage;

    int H_MIN = 0;

    int H_MAX = 44;

    int S_MIN = 0;

    int S_MAX = 291;

    int V_MIN = 210;

    int V_MAX = 286;

    int noOfRuns = 0;

    AtomicBoolean isProceed = new AtomicBoolean(true);

    public void startCapturing() {
        JFrame jFrame = new JFrame("Video");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(640, 480);
        WebCamCaptureTest panel = this;
        addSliders("H_MIN", panel, 0);
        addSliders("H_MAX", panel, 44);
        addSliders("S_MIN", panel, 0);
        addSliders("S_MAX", panel, 291);
        addSliders("V_MIN", panel, 199);
        addSliders("V_MAX", panel, 500);

        jFrame.setContentPane(panel);
        jFrame.setVisible(true);
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
        long i = 1;
        while (isProceed.get()) {
            //            isProceed.set(false);
            try {

                capture.read(frame);
                if (!frame.empty()) {
                    i = i < 1000 ? i + 1 : 1;
                    Mat gray = new Mat();
                    Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2HSV);
                    /*Mat blur = new Mat();
                    Imgproc.GaussianBlur(gray, blur, new Size(5, 5), 0);*/
                    Mat threshold = new Mat();
                    //                    Imgproc.threshold(gray, threshold, i, i, Imgproc.THRESH_BINARY_INV + Imgproc
                    // .THRESH_OTSU);
                    Core.inRange(gray, new Scalar(H_MIN, S_MIN, V_MIN), new Scalar(H_MAX, S_MAX, V_MAX), threshold);
                    System.out.println(H_MIN + " " + H_MAX + " | " + S_MIN + " " + S_MAX + " | " + V_MIN + " " + V_MAX + "");

                    Mat outFinal = threshold;


                    Rect rect = new Rect(200, 400, 5, 5);
                    Rect[] rects = new Rect[]{rect};
                    CamShifting camShifting = new CamShifting();
                    /*camShifting.create_tracked_object(frame, rects, camShifting);
                    RotatedRect rotatedRect = camShifting.camshift_track_face(frame, rects, camShifting);

                    Mat bgr = camShifting.bgr;
                    System.out.println(rotatedRect.center.x + " : " + rotatedRect.center.y);

                    Core.circle(bgr, rotatedRect.center, 20, new Scalar(5));

*/
                    MatOfByte frameBuffer = new MatOfByte();
                    Highgui.imencode(".jpg", outFinal, frameBuffer);
                    byte[] imgBytes = frameBuffer.toArray();
                    this.bufferedImage = ImageIO.read(new ByteArrayInputStream(imgBytes));
                    //                    System.out.println(outFinal.width() + " " + outFinal.height());
                    jFrame.setSize(outFinal.width() + 40, outFinal.height() + 40);
                    panel.repaint();
                    //                    readNewParams(bufferedReader);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addSliders(String name, WebCamCaptureTest panel, int initVal) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 1000, initVal);
        slider.setName(name);
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
                        case "H_MIN":
                            H_MIN = value;
                            break;
                        case "H_MAX":
                            H_MAX = value;
                            break;
                        case "S_MIN":
                            S_MIN = value;
                            break;
                        case "S_MAX":
                            S_MAX = value;
                            break;
                        case "V_MIN":
                            V_MIN = value;
                            break;
                        case "V_MAX":
                            V_MAX = value;
                            break;
                    }
                }
            }
        });
        slider.setEnabled(true);
        panel.add(slider);
    }

    private void readNewParams(BufferedReader bufferedReader) throws IOException {
        isProceed.set(true);
        if (noOfRuns > 1) {
            noOfRuns--;
            return;
        }
        System.out.println("Enter parameters - ");
        String line = bufferedReader.readLine();
        if (line == null || line.isEmpty()) {
            return;
        }

        String[] split = line.split(":");

        //0:44:0:291:200:500
        H_MIN = Integer.parseInt(split[0]);

        H_MAX = Integer.parseInt(split[1]);

        S_MIN = Integer.parseInt(split[2]);

        S_MAX = Integer.parseInt(split[3]);

        V_MIN = Integer.parseInt(split[4]);

        V_MAX = Integer.parseInt(split[5]);

        if (split.length == 7) {
            noOfRuns = Integer.parseInt(split[6]);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        BufferedImage image = bufferedImage;
        if (image == null) {
            return;
        }
        g.drawImage(image, 10, 10, image.getWidth(), image.getHeight(), this);
    }
}
