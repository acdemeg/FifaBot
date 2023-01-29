package org.bot.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.bot.Main.IMAGE_FORMAT;
import static org.bot.Main.LOG_IMAGES;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageUtils {

    /**
     * Load all images from {@link org.bot.Main#LOG_IMAGES} folder
     *
     * @return Map of pairs ImageId : BufferedImage
     */
    public static SortedMap<String, BufferedImage> getStringBufferedImageSortedMap() {
        SortedMap<String, BufferedImage> fileNameImageMap = new TreeMap<>();
        Arrays.stream(Objects.requireNonNull(LOG_IMAGES.listFiles())).forEach(file ->
                fileNameImageMap.put(file.getName().substring(0, file.getName().length() - 4), readImage(file))
        );
        return fileNameImageMap;
    }

    public static void saveImage(BufferedImage image, String dir, String name) throws IOException {
        File file = new File(dir, name);
        ImageIO.write(image, IMAGE_FORMAT, file);
    }

    @SneakyThrows
    private static BufferedImage readImage(File file) {
        return ImageIO.read(file);
    }

}
