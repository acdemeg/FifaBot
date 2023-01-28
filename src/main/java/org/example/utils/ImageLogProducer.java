package org.example.utils;

import lombok.Data;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.*;
import java.util.stream.Collectors;

public class ImageLogProducer {
    @Data
    private static class LogObject {
        private final String gameInfo;
        private final String gameActions;
        private final String decision;
    }

    private static BufferedImage combinedImage;
    private static final int FONT_SIZE = 20;
    private static final int HEIGHT_INFO_BLOCK = 220;
    private static final int SCALE_FACTOR = 5;
    private static final int SCALE_SIZE = SCALE_FACTOR * 258;
    private static final File logActions = new File("logs/fifa19bot.log");
    private static final File logImages = new File("logs/screenshots");
    private static final Map<String, LogObject> fileNameLogObjetMap = readLogs(
            Objects.requireNonNull(logImages.list()).length);

    @SneakyThrows
    public static void main(String[] args) {
        Map<String, BufferedImage> fileNameImageMap = Arrays.stream(Objects.requireNonNull(logImages.listFiles()))
                .collect(Collectors.toMap(
                        file -> file.getName().substring(0, file.getName().length() - 4), ImageLogProducer::getImage));
        fileNameImageMap.forEach(ImageLogProducer::processImage);
        // save full image
        String imageName = "logs/log_images/full.jpg";
        ImageIO.write(combinedImage, "jpg", new File(imageName));
    }

    @SneakyThrows
    private static void processImage(String imageId, BufferedImage image) {
        LogObject logObject = Optional.ofNullable(fileNameLogObjetMap.get(imageId))
                .orElse(new LogObject(
                        "GameInfo(activePlayer=[x=74,y=72], playmates=[[x=138,y=26], [x=97,y=40], [x=78,y=62], [x=147,y=68], [x=76,y=70], [x=106,y=70], [x=74,y=72], [x=25,y=78], [x=137,y=83], [x=115,y=84], [x=134,y=103], [x=141,y=106]], opposites=[[x=92,y=27], [x=133,y=32], [x=165,y=59], [x=80,y=63], [x=129,y=65], [x=77,y=67], [x=132,y=77], [x=234,y=78], [x=155,y=105], [x=121,y=107], [x=129,y=109]], ball=[x=99,y=90], isPlaymateBallPossession=false, isNobodyBallPossession=true, isShadingField=false, playmateSide=LEFT_PLAYMATE_SIDE)",
                        "[GameAction(controls=[NONE], actionTargetPlayer=[x=74,y=72]), GameAction(controls=[ATTACK_PROTECT_BALL], actionTargetPlayer=[x=74,y=72])]",
                        "GameAction(controls=[ATTACK_PROTECT_BALL], actionTargetPlayer=[x=74,y=72])"));
        // Step 1: Upscale image to 500%
        int newWidth = image.getWidth() * SCALE_FACTOR;
        int newHeight = image.getHeight() * SCALE_FACTOR;
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        BufferedImage logInfo = addLogInfo(logObject);

        // Step 4: Combine image with all previous images
        if (combinedImage == null) {
            combinedImage = combineImages(newImage, logInfo);
        } else {
            combinedImage = combineImages(combinedImage, newImage);
            combinedImage = combineImages(combinedImage, logInfo);
        }
    }

    private static BufferedImage addLogInfo(LogObject logs) {
        // Initialize image with specified height and width
        BufferedImage image = new BufferedImage(SCALE_SIZE, HEIGHT_INFO_BLOCK, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, SCALE_SIZE, HEIGHT_INFO_BLOCK);
        // Set font and color for text
        g2d.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
        // Draw log data on image
        float x = 10;
        float y = FONT_SIZE;
        g2d.setColor(Color.WHITE);
        y += addLogField(logs.getGameInfo(), g2d, x, y);
        g2d.setColor(Color.RED);
        addLogField(logs.getGameActions(), g2d, x, y);
        g2d.setColor(Color.GREEN);
        addLogField(logs.getDecision(), g2d, x, y + FONT_SIZE * 3);
        g2d.dispose();
        return image;
    }

    private static float addLogField(String logText, Graphics2D g2d, float x, float y) {
        AttributedString as = new AttributedString(logText);
        as.addAttribute(TextAttribute.FONT, g2d.getFont());
        as.addAttribute(TextAttribute.FOREGROUND, g2d.getColor());
        AttributedCharacterIterator aci = as.getIterator();
        FontRenderContext frc = g2d.getFontRenderContext();
        LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
        while (lbm.getPosition() < logText.length()) {
            TextLayout layout = lbm.nextLayout(SCALE_SIZE - x);
            y += layout.getAscent();
            layout.draw(g2d, x, y);
            y += layout.getDescent() + layout.getLeading();
        }
        return y;
    }

    private static BufferedImage combineImages(BufferedImage img1, BufferedImage img2) {
        int width = Math.max(img1.getWidth(), img2.getWidth());
        int height = img1.getHeight() + img2.getHeight();
        BufferedImage combinedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combinedImg.createGraphics();
        g2d.drawImage(img1, 0, 0, null);
        g2d.drawImage(img2, 0, img1.getHeight(), null);
        g2d.dispose();
        return combinedImg;
    }


    @SneakyThrows
    private static Map<String, LogObject> readLogs(int count) {

        final Map<String, LogObject> fileNameLogObjetMap = new HashMap<>(count);
        String imageId = null;
        String gameInfo = null;
        String gameActions = null;
        String decision = null;

        try (BufferedReader br = new BufferedReader(new FileReader(ImageLogProducer.logActions))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (line.contains("INFO")) {
                    if (line.contains("ImageId")) {
                        imageId = line.substring(15);
                    } else if (line.contains("GameInfo")) {
                        gameInfo = line.substring(6);
                    } else if (line.contains("[GameAction")) {
                        gameActions = line.substring(6);
                    } else if (line.contains("GameAction")) {
                        decision = line.substring(6);
                    }

                    if (gameInfo != null && gameActions != null && decision != null) {
                        fileNameLogObjetMap.put(imageId, new LogObject(gameInfo, gameActions, decision));
                        gameInfo = null;
                        gameActions = null;
                        decision = null;
                    }
                }
                if (count == fileNameLogObjetMap.size()) {
                    return fileNameLogObjetMap;
                }
            }
        }
        return fileNameLogObjetMap;
    }

    @SneakyThrows
    private static BufferedImage getImage(File file) {
        return ImageIO.read(file);
    }
}

