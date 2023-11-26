package org.bot;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.bot.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.bot.GameInfo.*;

@Log
public class Main {
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String IMAGE_FORMAT = "png";
    public static final String RAW_DATA_FORMAT = "dat";
    public static final Robot ROBOT = createRobot();
    public static final File LOG_IMAGES = new File(USER_HOME + "/logs/TestImages");
    public static final File LOG_ACTIONS = new File(USER_HOME + "/logs/fifa_bot.log");
    private static boolean isReplayerMode;
    private static boolean isLogging;

    @SneakyThrows
    private static Robot createRobot() {
        return new Robot();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        prepareEnv();
        setArgs(args);
        if (isReplayerMode) {
            runRePlayer();
        } else {
            ImageUtils.clearLogs();
            gameProcessing();
        }
    }

    private static void gameProcessing() throws IOException, InterruptedException {
        log.info("GAME START!");
        long start = System.currentTimeMillis();
        long year = 31104000000L;
        while (System.currentTimeMillis() - start < year) {
            Rectangle rectangle = new Rectangle(START_X, START_Y, WIDTH, HEIGHT);
            BufferedImage bufferedImage = ROBOT.createScreenCapture(rectangle);
            GameInfo gameInfo = new ImageAnalysis(bufferedImage).analyse();
            ActionProducer keyboardProducer = new DecisionMaker(gameInfo).decide();
            keyboardProducer.makeGameAction();
            if (isLogging) {
                logging(bufferedImage, gameInfo);
            }
        }
    }

    private static void logging(BufferedImage bufferedImage, GameInfo gameInfo)
            throws IOException, InterruptedException {
        String imageId = String.valueOf(System.nanoTime());
        log.info("ImageId: " + imageId);
        File file = new File(LOG_IMAGES.getPath(), imageId + "." + IMAGE_FORMAT);
        ImageIO.write(bufferedImage, IMAGE_FORMAT, file);
        ImageUtils.serialisationImageData(gameInfo.getPixels(), imageId);
        Thread.sleep(500);
    }

    private static void runRePlayer() {
        log.info("START Replayer!");
        ImageUtils.getImageNameDataMap().forEach((imageName, imageBuffer) -> {
            ImageUtils.setTempDataBuffer(imageBuffer);
            GameInfo gameInfo = new ImageAnalysis(null).analyse();
            DecisionMaker decisionMaker = new DecisionMaker(gameInfo);
            decisionMaker.decide();
        });
    }

    private static void setArgs(String[] args) {
        isLogging = Arrays.asList(args).contains("-logging");
        isReplayerMode = Arrays.asList(args).contains("-replayer");
    }

    private static void prepareEnv() throws IOException {
        log.info("Create directories if not exist");
        boolean dirSuccess = new File(USER_HOME + "/logs").mkdirs();
        boolean logActionSuccess = LOG_ACTIONS.createNewFile();
        boolean logLockSuccess = new File(USER_HOME + "/logs/fifa_bot.log.lck").createNewFile();
        boolean logImagesSuccess = LOG_IMAGES.mkdir();

        if (!(dirSuccess && logActionSuccess && logImagesSuccess && logLockSuccess)) {
            log.info("Can't create log dirs");
        }
    }
}
