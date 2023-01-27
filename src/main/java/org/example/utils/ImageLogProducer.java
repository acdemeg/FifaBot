package org.example.utils;

import lombok.Data;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ImageLogProducer {

    @Data
    private static class LogObject {
        private final String gameInfo;
        private final String gameActions;
        private final String decision;
    }

    private static final File logActions = new File("logs/fifa19bot.log");
    private static final File logImages = new File("logs/screenshots");
    private static final Map<String, LogObject> fileNameLogObjetMap = readLogs(
            Objects.requireNonNull(logImages.list()).length);

    public static void main(String[] args) {
        Map<String, BufferedImage> fileNameImageMap = Arrays.stream(Objects.requireNonNull(logImages.listFiles()))
                .collect(Collectors.toMap(
                        file -> file.getName().substring(0, file.getName().length() - 4), ImageLogProducer::getImage));
        fileNameImageMap.forEach(ImageLogProducer::processImage);
    }

    @SneakyThrows
    private static void processImage(String imageId, BufferedImage image) {
        LogObject logObject = fileNameLogObjetMap.get(imageId);
        // Step 1: Upscale image to 500%
        int newWidth = image.getWidth() * 5;
        int newHeight = image.getHeight() * 5;
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        // Step 2: Add black block to bottom of image
        int blockHeight = 200;
        g2d = newImage.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, newHeight + blockHeight, newWidth, blockHeight);
        g2d.dispose();
        // Step 3: Add text to black block
        g2d = newImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString("Game Info: " + logObject.getGameInfo(), 10, newHeight - 80);
        g2d.setColor(Color.RED);
        g2d.drawString("Game Actions: " + logObject.getGameActions(), 10, newHeight - 60);
        g2d.setColor(Color.GREEN);
        g2d.drawString("Decision: " + logObject.getDecision(), 10, newHeight - 40);
        g2d.dispose();
        // Step 4: Save image to file system
        String imageName = "logs/log_images/" + imageId + ".jpg";
        ImageIO.write(newImage, "jpg", new File(imageName));

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

