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

    public static final String IMAGE_FORMAT = "png";
    public static final Robot ROBOT = createRobot();

    @SneakyThrows
    private static Robot createRobot() {
        return new Robot();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        gameProcessing();
    }

    private static void gameProcessing() throws IOException, InterruptedException {
        log.info("GAME START!");
        ROBOT.waitForIdle();
        long start = System.currentTimeMillis();
        long year = 31104000000L;
        while (System.currentTimeMillis() - start < year) {
            Rectangle rectangle = new Rectangle(START_X, START_Y, WIDTH, HEIGHT);
            BufferedImage bufferedImage = ROBOT.createScreenCapture(rectangle);
            GameInfo gameInfo = new ImageAnalysis(bufferedImage).analyse();
            ActionProducer keyboardProducer = new DecisionMaker(gameInfo).decide();
            keyboardProducer.makeGameAction();
            /*
             * For logging
             */
            String imageId = String.valueOf(System.nanoTime());
            log.info("ImageId: " + imageId);
            saveImage(bufferedImage, "logs/screenshots", imageId + "." + IMAGE_FORMAT);
            Thread.sleep(500);
        }
    }

    @SuppressWarnings("unused")
    private static void makeScreenshot() throws IOException {
        Main.ROBOT.delay(10_000);
        Rectangle rectangle = new Rectangle(START_X, START_Y, WIDTH, HEIGHT);
        for (int i = 1; i < 101; i++) {
            Main.ROBOT.delay(2000);
            BufferedImage bufferedImage = Main.ROBOT.createScreenCapture(rectangle);
            saveImage(bufferedImage, "src/test/resources/screenshots", i + "." + IMAGE_FORMAT);
        }
    }

    public static void saveImage(BufferedImage image, String dir, String name) throws IOException {
        File file = new File(dir, name);
        ImageIO.write(image, IMAGE_FORMAT, file);
    }
}
