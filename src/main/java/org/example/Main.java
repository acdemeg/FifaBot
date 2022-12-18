package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.example.GameInfo.*;

@SuppressWarnings("InfiniteLoopStatement")
public class Main {



    public static void main(String[] args) throws AWTException, IOException {

//        BotStateSwitcher.createSwitcher();
        Robot robot = new Robot();
        robot.setAutoDelay(5);
        robot.setAutoWaitForIdle(true);

//        gameProcessing(robot);
        makeScreenshot(robot);

    }

    private static void gameProcessing(Robot robot) {
        while (true) {
            System.out.println(BotStateSwitcher.IS_ACTIVE);
            robot.delay(2000);
            if(BotStateSwitcher.IS_ACTIVE){
                robot.delay(2000);
                System.out.println(BotStateSwitcher.IS_ACTIVE);
                //robot.keyPress(KeyEvent.VK_D);
                //robot.keyRelease(KeyEvent.VK_D);
            }
        }
    }

    private static void makeScreenshot(Robot robot) throws IOException {
        robot.delay(5000);
        for (int i = 9; i < 21; i++){
            robot.delay(2000);
            Rectangle rectangle = new Rectangle(startX, startY, width, height);
            BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
            File file = new File("screenshots", i + ".jpg");
            ImageIO.write(bufferedImage, "png", file);
        }
    }
}
