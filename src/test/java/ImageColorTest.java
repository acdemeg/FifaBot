import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.example.Main.*;

public class ImageColorTest {

    private final BufferedImage bufferedImage;

    public ImageColorTest() throws IOException {
        File file = new File("screenshots", "1.jpg");
        this.bufferedImage = ImageIO.read(file);
    }

    @Test
    void checkColorTest() {
        Color startPixel = new Color(bufferedImage.getRGB(0, 0));
        Color endPixel = new Color(bufferedImage.getRGB(257, 152));
        Color pixelDarkField = new Color(bufferedImage.getRGB(31, 58));
        Color pixelLightField = new Color(bufferedImage.getRGB(229, 54));
        Color pixelBall = new Color(bufferedImage.getRGB(77, 36));
        Color pixelActivePlayer = new Color(bufferedImage.getRGB(73, 32));
        Color pixelBoundOfPlayer = new Color(bufferedImage.getRGB(98, 6));
        Color pixelPlaymate = new Color(bufferedImage.getRGB(129, 61));
        Color pixelOpposing = new Color(bufferedImage.getRGB(135, 67));
        Color pixelCenterField = new Color(bufferedImage.getRGB(128, 77));

        Assertions.assertEquals(startPixel.getRGB(), new Color(194,204,185).getRGB());
        Assertions.assertEquals(endPixel.getRGB(), new Color(174,185,163).getRGB());
        Assertions.assertEquals(pixelDarkField.getRGB(), new Color(52,74,27).getRGB());
        Assertions.assertEquals(pixelLightField.getRGB(), new Color(57,77,35).getRGB());
        Assertions.assertEquals(pixelBall.getRGB(), new Color(255,186,0).getRGB());
        Assertions.assertEquals(pixelActivePlayer.getRGB(), new Color(0,172,172).getRGB());
        Assertions.assertEquals(pixelBoundOfPlayer.getRGB(), new Color(254,254,254).getRGB());
        Assertions.assertEquals(pixelPlaymate.getRGB(), new Color(198,41,44).getRGB());
        Assertions.assertEquals(pixelOpposing.getRGB(), new Color(40,45,77).getRGB());
        Assertions.assertEquals(pixelCenterField.getRGB(), new Color(208,210,204).getRGB());
    }

    /**
     * Concept:
     * 1-Step: Iterating by {@code bufferedImage} rectangle top-down and left right straight
     *  and get pixel integer color with x,y coordinates.
     * 2-Step: Check whether pixel represent bound of player.
     * 3-Step: Check whether exist Point which can be center coordinates of Player
     *  and located below and to the right then given (x,y), if Yes - current {@code x} replace {@code  endPlayerBound}
     *  and continue for cycle, if No - starting 4 Step.
     * 4-Step: Find middle of bound Player and trying to add in Set new Player via {@code setPlayerCoordinate} method.
     * 5-Step: If already exist point which locate in radius = 8 from given (x, y) point then set of players not modify,
     *  else add new coordinate in corresponding Set in depend on player belong to.
     */
    @Test
    void findPlayersTest() {
        Set<Point> playmates = new HashSet<>(11);
        Set<Point> opposites = new HashSet<>(11);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bufferedImage.getRGB(x, y);
                if (pixel > boundOfPlayerColor && pixel < Color.white.getRGB()) {

                    if (isExistRightBottomPoint(x, y, playmates, opposites)) {
                        x = getEndPlayerBound(x + 1, y);
                    }
                    else {
                        int endPlayerBound = getEndPlayerBound(x + 1, y);
                        int middlePlayerBound = endPlayerBound - ( (endPlayerBound - x) / 2);
                        boolean isAdd = setPlayerCoordinate(middlePlayerBound, y + 4, playmates, opposites);
                        x = isAdd ? endPlayerBound : x;
                    }
                }
            }
        }

        Assertions.assertEquals(11, playmates.size());
        Assertions.assertEquals(11, opposites.size());
    }

    private boolean isExistRightBottomPoint(int x, int y, Set<Point> playmates, Set<Point> opposites) {
        Stream<Point> players = Stream.concat(playmates.stream(), opposites.stream());
        return players.anyMatch(point -> (point.getX() >= x) && (point.getY() >= y));
    }

    private int getEndPlayerBound(int x, int y) {
        if (x < width) {
            int pixel = bufferedImage.getRGB(x, y);
            if (pixel > boundOfPlayerColor && pixel < Color.white.getRGB()) {
                return getEndPlayerBound(x + 1, y);
            }
            return x;
        }
        return x;
    }

    private boolean setPlayerCoordinate(int x, int y, Set<Point> playmates, Set<Point> opposites) {
        if (y < height) {
            int pixel = bufferedImage.getRGB(x, y);

            if (pixel == playmateColor) {
                boolean isExistPoint = playmates.stream().anyMatch(point -> point.distance(x, y) < 8);
                return isExistPoint || playmates.add(new Point(x, y));
            }
            else if (pixel == oppositeColor) {
                boolean isExistPoint = opposites.stream().anyMatch(point -> point.distance(x, y) < 8);
                return isExistPoint || opposites.add(new Point(x, y));
            }
            return false;
        }
        return false;
    }

}
