package org.bot;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.bot.debug.ImageLogProducer;
import org.bot.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.LogManager;

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
    private static boolean isLoggingMode;
    private static boolean isProductionMode;
    private static boolean isVisualLogMode;

    @SneakyThrows
    private static Robot createRobot() {
        return new Robot();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        prepareEnv();
        setArgs(args);
        if (isVisualLogMode) {
            ImageLogProducer.produce();
            return;
        }
        if (isProductionMode) {
            LogManager.getLogManager().reset();
        }
        if (isReplayerMode) {
            LogManager.getLogManager().reset();
            runRePlayer();
            return;
        }
        ImageUtils.clearLogs();
        gameProcessing();
    }

    @SuppressWarnings("BusyWait")
    private static void gameProcessing() throws IOException, InterruptedException {
        AtomicBoolean exit = new AtomicBoolean(false);
        inputProcessing(exit);
        while (!exit.get()) {
            log.info("          ***------------------------------------------------------------***            ");
            Rectangle rectangle = new Rectangle(START_X, START_Y, WIDTH, HEIGHT);
            BufferedImage bufferedImage = ROBOT.createScreenCapture(rectangle);
            GameInfo gameInfo = new ImageAnalysis(bufferedImage).analyse();
            ActionProducer keyboardProducer = new DecisionMaker(gameInfo).decide();
            keyboardProducer.makeGameAction();
            if (isLoggingMode) {
                logging(bufferedImage, gameInfo);
            }
            Thread.sleep(300);
        }
        // release all keys on exit
        ActionProducer.releaseAll();
    }

    private static void inputProcessing(AtomicBoolean exit) {
        new Thread(() -> {
            System.out.println("GAME STARTED!");
            while (!exit.get()) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("To end the program press \"Enter\":");
                scanner.nextLine();
                exit.set(true);
            }
        }).start();
    }

    private static void logging(BufferedImage bufferedImage, GameInfo gameInfo) throws IOException {
        String imageId = String.valueOf(System.nanoTime());
        log.info("ImageId: " + imageId);
        File file = new File(LOG_IMAGES.getPath(), imageId + "." + IMAGE_FORMAT);
        ImageIO.write(bufferedImage, IMAGE_FORMAT, file);
        ImageUtils.serialisationImageData(gameInfo.getPixels(), imageId);
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
        isLoggingMode = Arrays.asList(args).contains("-logging");
        isReplayerMode = Arrays.asList(args).contains("-replayer");
        isProductionMode = Arrays.asList(args).contains("-production");
        isVisualLogMode = Arrays.asList(args).contains("-visual-log");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void prepareEnv() throws IOException {
        Locale.setDefault(Locale.ENGLISH);
        new File(USER_HOME + "/logs").mkdirs();
        LOG_ACTIONS.createNewFile();
        new File(USER_HOME + "/logs/fifa_bot.log.lck").createNewFile();
        LOG_IMAGES.mkdir();
        LogManager.getLogManager().readConfiguration(
                Main.class.getClassLoader().getResourceAsStream("logging.properties")
        );
    }
}
