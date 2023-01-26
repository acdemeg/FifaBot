import org.example.GameInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

class ImageAnalysisTest {

    private final Set<Integer> notValidImageSet = Set.of(1, 2, 12, 13, 14, 15, 16, 17, 18, 38, 54, 61, 62, 90);
    private final Set<Integer> shadingImageSet = Set.of(10, 51, 52, 53, 55, 56, 80, 81, 88, 91, 92, 93, 94, 96, 97, 98);
    private final Set<Integer> notFullSetPlayers = Set.of(37, 63, 64, 65, 66, 69, 74, 83, 95);
    private final Set<Integer> playmateBallPossessionSet = Set.of(3, 4, 6, 7, 8, 9, 24);
    private final Set<Integer> oppositeBallPossessionSet = Set.of(23, 26);
    private final Set<Integer> nobodyBallPossessionSet = Set.of(5, 11, 19, 20, 21, 22);

    @ParameterizedTest
    @MethodSource("provideImageNumbers")
    void analyseTest(Integer number) throws IOException {
        GameInfo gameInfo = TestUtils.getGameInfo(number);
        if (notValidImageSet.contains(number)) {
            return;
        }
        if (shadingImageSet.contains(number)) {
            Assertions.assertTrue(gameInfo.isShadingField());
            return;
        }
        if (notFullSetPlayers.contains(number)) {
            Assertions.assertTrue(gameInfo.getPlaymates().size() >= 10);
            Assertions.assertTrue(gameInfo.getOpposites().size() >= 10);
            Assertions.assertFalse(gameInfo.isShadingField());
            return;
        }
        Assertions.assertEquals(11, gameInfo.getPlaymates().size());
        Assertions.assertEquals(11, gameInfo.getOpposites().size());
        Assertions.assertNotNull(gameInfo.getActivePlayer());
        Assertions.assertNotNull(gameInfo.getBall());
        Assertions.assertFalse(gameInfo.isShadingField());

        if (playmateBallPossessionSet.contains(number)) {
            Assertions.assertTrue(gameInfo.isPlaymateBallPossession());
            Assertions.assertFalse(gameInfo.isNobodyBallPossession());
        } else if (oppositeBallPossessionSet.contains(number)) {
            Assertions.assertFalse(gameInfo.isPlaymateBallPossession());
            Assertions.assertFalse(gameInfo.isNobodyBallPossession());
        } else if (nobodyBallPossessionSet.contains(number)) {
            Assertions.assertFalse(gameInfo.isPlaymateBallPossession());
            Assertions.assertTrue(gameInfo.isNobodyBallPossession());
        }
    }

    private static Stream<Integer> provideImageNumbers() {
        return Stream.iterate(1, x -> x + 1).limit(100);
    }
}
