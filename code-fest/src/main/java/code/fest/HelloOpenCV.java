package code.fest;

import org.opencv.core.Core;

import java.lang.reflect.Field;
import java.util.Arrays;

public class HelloOpenCV {

    private static String libPath = "/home/prabath/Projects/code-fest/src/main/resources/";

    public static void main(String[] args) {
        System.out.println("Hello, OpenCV");

        // Load the native library.
        //
        loadLibraries();
//        new Ball().startBallTracking();
        new WebCamCaptureTest().startCapturing();
    }

    private static void loadLibraries() {
        try {
            Field usrPath = ClassLoader.class.getDeclaredField("usr_paths");
            usrPath.setAccessible(true);
            String[] paths = (String[]) usrPath.get(null);
            String[] attachedPath = Arrays.copyOf(paths, paths.length + 1);
            attachedPath[attachedPath.length - 1] = libPath;
            usrPath.set(null, attachedPath);
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (Exception e) {
            System.out.println("Error occurred while loading libraries. " + e);
        }
    }
}

