package org.example;

import java.awt.*;

public class Utils {

    private Utils(){}

    public static void pixelLogging(int pixel, int x, int y, Color color) {
        System.out.println(
                "Pixel RGB = " + pixel + ", " +
                "Position = [" + x + "," + y + "], " +
                "Color(r,g,b) = [" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + "]"
        );
    }
}
