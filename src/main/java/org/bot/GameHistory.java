package org.bot;

import lombok.*;
import org.bot.utils.ImageUtils;

import java.awt.*;
import java.util.Collections;
import java.util.Optional;

/**
 * This class represent prev game states and prev targets decision
 */
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameHistory {
    @Getter
    @Setter
    private static GameInfo prevGameInfo;
    @Getter
    @Setter
    private static Point prevActionTarget;
    @Getter
    @Setter
    private static GameAction prevGameAction;
    @Getter
    @Setter
    private static int actionRepeats;
    @Getter
    @Setter
    private static GameAction notReleasedGameAction;
    @Getter
    @Setter
    private static boolean isContinuousAction;
	@Getter
    @Setter
    private static boolean isPossessionChangedOnTrue;
    @Getter
    @Setter
    private static int ballPossessionChecksCount;

    public static String toStringStatic() {
        GameAction mock = new GameAction(Collections.emptyList(), null);
        return "isContinuousAction=" + isContinuousAction + ", "
                + "isPossessionChangedOnTrue=" + isPossessionChangedOnTrue + ", "
                + "ballPossessionRepeatCount=" + ballPossessionChecksCount + ", "
                + "actionRepeats=" + actionRepeats + ", "
                + "notReleasedGameAction=" + Optional.ofNullable(notReleasedGameAction).orElse(mock) + ", "
                + "prevGameAction=" + Optional.ofNullable(prevGameAction).orElse(mock) + ", "
                + "prevActionTarget=" + Optional.ofNullable(prevActionTarget).orElse(new Point()) + ", "
                + "prevGameInfo=" + ImageUtils.pinchLogs(
                        Optional.ofNullable(prevGameInfo).orElse(new GameInfo()).toString(), 0) + ", ";

    }

    public static void reset() {
        prevGameInfo = null;
        prevActionTarget = null;
        prevGameAction = null;
        actionRepeats = 0;
        notReleasedGameAction = null;
        isContinuousAction = false;
        isPossessionChangedOnTrue = false;
        ballPossessionChecksCount = 0;
    }
}
