package org.bot.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.bot.Main.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageUtils {

    @Setter
    private static int[][] tempDataBuffer;

    /**
     * Load all images from {@link org.bot.Main#LOG_IMAGES} folder
     *
     * @return Map of pairs ImageId : BufferedImage
     */
    public static SortedMap<String, BufferedImage> getStringBufferedImageSortedMap() {
        SortedMap<String, BufferedImage> fileNameImageMap = new TreeMap<>();
        Arrays.stream(Objects.requireNonNull(LOG_IMAGES.listFiles())).forEach(file -> {
            if (file.getName().endsWith(IMAGE_FORMAT)) fileNameImageMap.put(cropExt(file), readImage(file));
        });
        return fileNameImageMap;
    }

    /**
     * Remove all images in {@link org.bot.Main#LOG_IMAGES} folder
     */
    public static void clearLogs() throws IOException {
        try(FileChannel ignored = FileChannel.open(Paths.get(LOG_ACTIONS.getAbsolutePath()),
                                                   StandardOpenOption.WRITE).truncate(0)) {
            File[] files = LOG_IMAGES.listFiles();
            if (files != null) { //some JVMs return null for empty dirs
                for (File f : files) {
                    if (f.isDirectory()) {
                        clearLogs();
                    } else {
                        Files.delete(f.toPath());
                    }
                }
            }
        }
    }

    public static SortedMap<String, int[][]> getImageNameDataMap() {
        SortedMap<String, int[][]> fileNameImageMap = new TreeMap<>();
        Arrays.stream(Objects.requireNonNull(LOG_IMAGES.listFiles())).forEach(file -> {
            if (file.getName().endsWith(RAW_DATA_FORMAT)) {
                fileNameImageMap.put(cropExt(file), deserializationImageData(cropExt(file)));
            }
        });
        return fileNameImageMap;
    }

    public static String pinchLogs(String line, int beginIndex) {
        return line.substring(beginIndex)
                .replace("java.awt.Point", "")
                .replace("x=", "")
                .replace("y=", "")
                .replace("delay=", "")
                .replace("dela", "");
    }

    @SneakyThrows
    public static void serialisationImageData(int[][] dataBuffer, String imageId) {
        File file = new File(LOG_IMAGES.getPath(), imageId + ".dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(dataBuffer);
        }
    }

    @SneakyThrows
    public static int[][] deserializationImageData(String imageId) {
        File file = new File(LOG_IMAGES.getPath(), imageId + ".dat");
        try (ObjectInputStream iis = new ObjectInputStream(new FileInputStream(file))) {
            return (int[][]) iis.readObject();
        }
    }

    public static int getRGB(BufferedImage image, int x, int y) {
        // tempDataBuffer use for replayer
        return image == null ? tempDataBuffer[x][y] : image.getRGB(x, y);
    }

    @SneakyThrows
    private static BufferedImage readImage(File file) {
        return ImageIO.read(file);
    }

    private static String cropExt(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
}
