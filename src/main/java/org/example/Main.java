package org.example;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.example.GameInfo.*;

@Log
public class Main {

    public static final Robot ROBOT = createRobot();

    @SneakyThrows
    private static Robot createRobot() {
        return new Robot();
    }

    public static void main(String[] args) throws IOException {
        gameProcessing();
    }

    private static void gameProcessing() throws IOException {
        log.fine("GAME START!");
        long start = System.currentTimeMillis();
        long year = 31104000000L;
        while (System.currentTimeMillis() - start < year) {
            Rectangle rectangle = new Rectangle(START_X, START_Y, WIDTH, HEIGHT);
            BufferedImage bufferedImage = ROBOT.createScreenCapture(rectangle);
            /*
             * For logging
             */
            String imageId = String.valueOf(System.nanoTime());
            log.info("ImageId: " + imageId);
            saveImage(bufferedImage, "logs/screenshots", imageId + ".jpg");

            GameInfo gameInfo = new ImageAnalysis(bufferedImage).analyse();
            ActionProducer keyboardProducer = new DecisionMaker(gameInfo).decide();
            keyboardProducer.makeGameAction();
        }
    }

    @SuppressWarnings("unused")
    private static void makeScreenshot() throws IOException {
        Main.ROBOT.delay(10_000);
        Rectangle rectangle = new Rectangle(START_X, START_Y, WIDTH, HEIGHT);
        for (int i = 1; i < 101; i++) {
            Main.ROBOT.delay(2000);
            BufferedImage bufferedImage = Main.ROBOT.createScreenCapture(rectangle);
            saveImage(bufferedImage, "src/test/resources/screenshots", i + ".jpg");
        }
    }

    public static void saveImage(BufferedImage image, String dir, String name) throws IOException {
        File file = new File(dir, name);
        ImageIO.write(image, "jpg", file);
    }
}
