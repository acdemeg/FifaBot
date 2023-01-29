package org.bot;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.bot.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.bot.GameInfo.*;

@Log
public class Main {

    public static final String IMAGE_FORMAT = "png";
    public static final Robot ROBOT = createRobot();
    public static final File LOG_IMAGES = new File("logs/screenshots");
    public static final File LOG_ACTIONS = new File("logs/fifa19bot.log");
    private static final boolean IS_REPLAYER_MODE = true;
    private static final boolean IS_LOGGING = true;

    @SneakyThrows
    private static Robot createRobot() {
        return new Robot();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (IS_REPLAYER_MODE) {
            runRePlayer();
        } else gameProcessing();
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
            if (IS_LOGGING) {
                logging(bufferedImage);
            }
        }
    }

    private static void logging(BufferedImage bufferedImage) throws IOException, InterruptedException {
        String imageId = String.valueOf(System.nanoTime());
        log.info("ImageId: " + imageId);
        ImageUtils.saveImage(bufferedImage, LOG_IMAGES.getPath(), imageId + "." + IMAGE_FORMAT);
        Thread.sleep(500);
    }

    private static void runRePlayer() {
        log.info("START RePlayer!");
        ROBOT.waitForIdle();
        ImageUtils.getStringBufferedImageSortedMap().forEach((name, image) -> {
            GameInfo gameInfo = new ImageAnalysis(image).analyse();
            DecisionMaker decisionMaker = new DecisionMaker(gameInfo);
            decisionMaker.decide();
        });
    }
}
