package org.example;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("InfiniteLoopStatement")
public class Main {

    public static void main(String[] args) throws AWTException, IOException {

        //BotStateSwitcher.createSwitcher();
        Robot robot = new Robot();
        robot.setAutoDelay(5);
        robot.setAutoWaitForIdle(true);

        //gameProcessing(robot);
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
        for (int i = 0; i < 10; i++){
            robot.delay(1000);
            Rectangle rectangle = new Rectangle(831,882,258,153);
            BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
            File file = new File("screenshots", System.currentTimeMillis() + ".jpg");
            ImageIO.write(bufferedImage, "png", file);
        }
    }
}
