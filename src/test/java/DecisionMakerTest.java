import org.example.DecisionMaker;
import org.example.GameAction;
import org.example.GameInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.example.enums.ControlsEnum.*;

class DecisionMakerTest {

    @ParameterizedTest
    @MethodSource("provideImageNumbers")
    void decideTest(Integer number) throws IOException {
        GameInfo gameInfo = TestUtils.getGameInfo(number);
        DecisionMaker decisionMaker = new DecisionMaker(gameInfo);
        decisionMaker.decide();
        Set<GameAction> gameActions = decisionMaker.getGameActions();

        switch (number) {
            case 1: {
                GameAction attackShortPass = new GameAction(
                        List.of(MOVE_UP, MOVE_RIGHT, ATTACK_SHORT_PASS_HEADER), new Point(98, 10));
                GameAction moveRight = new GameAction(List.of(MOVE_RIGHT), new Point(75, 35));
                Assertions.assertTrue(gameActions.contains(attackShortPass) && gameActions.contains(moveRight));
                break;
            }
            case 3: {
                GameAction attackShortPass = new GameAction(
                        List.of(MOVE_RIGHT, ATTACK_SHORT_PASS_HEADER), new Point(129, 60));
                GameAction moveRight = new GameAction(List.of(MOVE_RIGHT), new Point(81, 54));
                Assertions.assertTrue(gameActions.contains(attackShortPass) && gameActions.contains(moveRight));
                break;
            }
            case 7: {
                GameAction attackShortPass = new GameAction(
                        List.of(MOVE_DOWN, MOVE_RIGHT, ATTACK_SHORT_PASS_HEADER), new Point(125, 123));
                GameAction moveRight = new GameAction(List.of(MOVE_RIGHT), new Point(65, 101));
                Assertions.assertTrue(gameActions.contains(attackShortPass) && gameActions.contains(moveRight));
            }
        }

    }

    private static Stream<Integer> provideImageNumbers() {
        return Stream.of(1, 3, 7);
    }
}
