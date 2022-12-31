package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.example.GameInfo.*;

public class Main {

    public static void main(String[] args) throws AWTException, IOException {

        BotStateSwitcher switcher = BotStateSwitcher.createSwitcher();
        Robot robot = new Robot();
        robot.setAutoDelay(5);
        robot.setAutoWaitForIdle(true);

        gameProcessing(robot, switcher);
        makeScreenshot(robot);

    }

    private static void gameProcessing(Robot robot, BotStateSwitcher switcher) {
        while (true) {
            Rectangle rectangle = new Rectangle(START_X, START_Y, WIDTH, HEIGHT);
            BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
            ImageAnalysis analysis = new ImageAnalysis(bufferedImage);
            GameInfo gameInfo = analysis.analyse();
            DecisionMaker decisionMaker = new DecisionMaker(gameInfo);
            ActionProducer actionProducer = decisionMaker.getActionProducer();
            robot.keyPress(KeyEvent.VK_D);
        }
    }

    private static void makeScreenshot(Robot robot) throws IOException {
        robot.delay(10_000);
        Rectangle rectangle = new Rectangle(START_X, START_Y, WIDTH, HEIGHT);
        for (int i = 21; i < 101; i++){
            robot.delay(2000);
            BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
            File file = new File("screenshots", i + ".jpg");
            ImageIO.write(bufferedImage, "png", file);
        }
    }
}
