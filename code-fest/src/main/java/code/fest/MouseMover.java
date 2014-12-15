package code.fest;

import javax.swing.*;
import java.awt.*;

/**
 * @author: prabath
 */
public class MouseMover extends JPanel {

    private Robot mouse;

    private MouseMover panel;

    public MouseMover() {
        try {
            mouse = new Robot();
            JFrame jFrame = new JFrame("Mouse");
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setSize(1000, 1000);
            jFrame.setContentPane(this);
            jFrame.setVisible(false);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public MouseMover(Robot mouse) {
        this.mouse = mouse;
    }

    public void moveMouse(int x, int y) {
        mouse.mouseMove(x, y);
    }
}
