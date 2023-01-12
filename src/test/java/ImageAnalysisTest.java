import org.example.GameInfo;
import org.example.ImageAnalysis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

class ImageAnalysisTest {

    Set<Integer> notValidImageSet = Set.of(38, 39, 40, 41, 42, 43, 51, 59, 60, 64, 65, 83);
    Set<Integer> notFullSetPlayers = Set.of(45, 84, 85, 86, 87, 91, 92);
    Set<Integer> playmateBallPossessionSet = Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 13, 14, 16, 19, 20, 22, 23, 25, 30);
    Set<Integer> oppositeBallPossessionSet = Set.of(15, 26, 27, 28, 34, 35, 36);
    Set<Integer> nobodyBallPossessionSet = Set.of(10, 11, 17, 18, 21, 24, 29, 31, 32, 33);

    @Test
    void colorTest() throws IOException {
        File file = new File("screenshots", "1.jpg");
        BufferedImage bufferedImage = ImageIO.read(file);

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

    @ParameterizedTest
    @MethodSource("provideImageNumbers")
    void analyseTest(Integer number) throws IOException {
        File file = new File("screenshots", number + ".jpg");
        BufferedImage bufferedImage = ImageIO.read(file);
        ImageAnalysis imageAnalysis = new ImageAnalysis(bufferedImage);
        GameInfo gameInfo = imageAnalysis.analyse();
        if (notValidImageSet.contains(number)) {
            return;
        }
        if (notFullSetPlayers.contains(number)) {
            Assertions.assertTrue(gameInfo.getPlaymates().size() >= 10);
            Assertions.assertTrue(gameInfo.getOpposites().size() >= 10);
            return;
        }
        Assertions.assertEquals(11, gameInfo.getPlaymates().size());
        Assertions.assertEquals(11, gameInfo.getOpposites().size());
        Assertions.assertNotNull(gameInfo.getActivePlayer());
        Assertions.assertNotNull(gameInfo.getBall());

        if (playmateBallPossessionSet.contains(number)) {
            Assertions.assertTrue(gameInfo.isPlaymateBallPossession());
            Assertions.assertFalse(gameInfo.isNobodyBallPossession());
        }
        else if (oppositeBallPossessionSet.contains(number)) {
            Assertions.assertFalse(gameInfo.isPlaymateBallPossession());
            Assertions.assertFalse(gameInfo.isNobodyBallPossession());
        }
        else if (nobodyBallPossessionSet.contains(number)) {
            Assertions.assertFalse(gameInfo.isPlaymateBallPossession());
            Assertions.assertTrue(gameInfo.isNobodyBallPossession());
        }

    }

    private static Stream<Integer> provideImageNumbers() {
        return Stream.iterate(1, x -> x + 1).limit(20);
    }
}
