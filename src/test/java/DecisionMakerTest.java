import org.bot.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.bot.enums.ControlsEnum.*;

class DecisionMakerTest {

    @ParameterizedTest
    @MethodSource("TestUtils#provideImageNumbers")
    void decideTest(Integer number) throws IOException {
        GameInfo gameInfo = TestUtils.getGameInfo(number);
        DecisionMaker decisionMaker = new DecisionMaker(gameInfo);
        ActionProducer producer = decisionMaker.decide();
        Set<GameAction> gameActions = decisionMaker.getGameActions();
        GameAction action = producer.gameAction();
        GameHistory.reset();

        switch (number) {
            case 3 -> {
                GameAction attackShortPass = new GameAction(
                        List.of(MOVE_UP, ATTACK_SHORT_PASS_HEADER), new Point(106, 55));
                GameAction moveRight = new GameAction(List.of(MOVE_RIGHT), new Point(104, 78));
                Assertions.assertTrue(gameActions.contains(attackShortPass) && gameActions.contains(moveRight));
                Assertions.assertEquals(moveRight, action);
            }
            case 4 -> {
                GameAction attackShortPass = new GameAction(
                        List.of(MOVE_UP, MOVE_RIGHT, ATTACK_SHORT_PASS_HEADER), new Point(122, 61));
                GameAction moveRight = new GameAction(List.of(MOVE_RIGHT), new Point(105, 86));
                Assertions.assertTrue(gameActions.contains(attackShortPass) && gameActions.contains(moveRight));
                Assertions.assertEquals(moveRight, action);
            }
            case 5 -> {
                GameAction moveRight = new GameAction(List.of(MOVE_RIGHT), new Point(68, 51));
                GameAction attackProtectBall = new GameAction(List.of(ATTACK_PROTECT_BALL), new Point(68, 51));
                Assertions.assertTrue(gameActions.contains(attackProtectBall));
                Assertions.assertEquals(moveRight, action);
            }
            case 6 -> {
                GameAction attackShortPass = new GameAction(
                        List.of(MOVE_RIGHT, ATTACK_SHORT_PASS_HEADER), new Point(110, 16));
                GameAction moveRight = new GameAction(List.of(MOVE_RIGHT), new Point(76, 15));
                Assertions.assertTrue(gameActions.contains(attackShortPass) && gameActions.contains(moveRight));
                Assertions.assertEquals(moveRight, action);
            }
            case 7 -> {
                GameAction attackShortPass = new GameAction(
                        List.of(MOVE_DOWN, ATTACK_SHORT_PASS_HEADER), new Point(92, 43));
                GameAction moveRight = new GameAction(List.of(MOVE_RIGHT), new Point(91, 18));
                Assertions.assertTrue(gameActions.contains(attackShortPass) && gameActions.contains(moveRight));
                Assertions.assertEquals(moveRight, action);
            }
            case 8 -> {
                GameAction moveRight = new GameAction(List.of(MOVE_RIGHT), new Point(102, 45));
                Assertions.assertEquals(moveRight, action);
            }
            case 9 -> {
                GameAction attackShortPass = new GameAction(
                        List.of(MOVE_UP, ATTACK_SHORT_PASS_HEADER), new Point(127, 7));
                Assertions.assertEquals(attackShortPass, action);
            }
            case 10 -> {
                GameAction moveUpRight = new GameAction(
                        List.of(MOVE_UP, MOVE_RIGHT), new Point(152, 133));
                Assertions.assertEquals(moveUpRight, action);
            }
        }
    }
}
