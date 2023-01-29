package org.bot.debug;

import lombok.Data;
import lombok.SneakyThrows;
import org.bot.enums.GeomEnum;
import org.bot.utils.ImageUtils;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.bot.Main.*;

public class ImageLogProducer {
    @Data
    private static class LogObject {
        private final String gameInfo;
        private final String gameActions;
        private final String decision;
    }
    private static BufferedImage combinedImage;
    private static final int FONT_SIZE = 20;
    private static final int HEIGHT_INFO_BLOCK = 320;
    private static final int SCALE_FACTOR = 5;
    private static final int SCALE_SIZE = SCALE_FACTOR * 258;
    private static final Map<String, LogObject> fileNameLogObjetMap = readLogs(
            Objects.requireNonNull(LOG_IMAGES.list()).length);

    @SneakyThrows
    public static void main(String[] args) {
        ImageUtils.getStringBufferedImageSortedMap().forEach(ImageLogProducer::processImage);
        // save full image
        String imageName = "logs/log_images/game_history_log." + IMAGE_FORMAT;
        ImageIO.write(combinedImage, IMAGE_FORMAT, new File(imageName));
    }

    @SneakyThrows
    private static void processImage(String imageId, BufferedImage image) {
        LogObject logObject = Optional.ofNullable(fileNameLogObjetMap.get(imageId))
                .orElse(new LogObject("No", "present", "data"));
        // Step 1: Upscale image to 500%
        int newWidth = image.getWidth() * SCALE_FACTOR;
        int newHeight = image.getHeight() * SCALE_FACTOR;
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        BufferedImage logInfo = addLogInfo(logObject, imageId);

        // Step 4: Combine image with all previous images
        BufferedImage temp = combineImages(newImage, logInfo, GeomEnum.BOTTOM);
        combinedImage = combineImages(combinedImage, temp, GeomEnum.RIGHT);
    }

    private static BufferedImage addLogInfo(LogObject logs, String imageId) {
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
        y += FONT_SIZE * 4;
        addLogField(logs.getDecision(), g2d, x, y);
        g2d.setColor(Color.ORANGE);
        y += FONT_SIZE * 2;
        addLogField("ImageId: " + imageId, g2d, x, y);
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

    private static BufferedImage combineImages(BufferedImage img1, BufferedImage img2, GeomEnum direction) {
        if (img1 == null) {
            return img2;
        }
        int width = Math.max(img1.getWidth(), img2.getWidth());
        int height = img1.getHeight() + img2.getHeight();
        int x = 0;
        int y = img1.getHeight();
        if (direction.equals(GeomEnum.RIGHT)) {
            width = img1.getWidth() + img2.getWidth();
            height = Math.max(img1.getHeight(), img2.getHeight());
            x = img1.getWidth();
            y = 0;
        }
        BufferedImage combinedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combinedImg.createGraphics();
        g2d.drawImage(img1, 0, 0, null);
        g2d.drawImage(img2, x, y, null);
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

        try (BufferedReader br = new BufferedReader(new FileReader(LOG_ACTIONS))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (line.contains("INFO")) {
                    if (line.contains("ImageId")) {
                        imageId = line.substring(15);
                    } else if (line.contains("GameInfo")) {
                        gameInfo = ImageUtils.pinchLogs(line, 6);
                    } else if (line.contains("[GameAction")) {
                        gameActions = ImageUtils.pinchLogs(line, 6);
                    } else if (line.contains("GameAction")) {
                        decision = ImageUtils.pinchLogs(line, 6);
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
}

