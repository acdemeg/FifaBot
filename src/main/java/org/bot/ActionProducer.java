package org.bot;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bot.enums.ControlsEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;

import static org.bot.GameAction.CONTROLS_ENUM_KEY_CODE_MAP;
import static org.bot.Main.ROBOT;

/**
 * This class take responsible for events generation.
 * Now is available only keyboard actions
 */
@Data
@RequiredArgsConstructor
public class ActionProducer {

    private final GameAction gameAction;

    public void makeGameAction() {
        if (GameHistory.getNotReleasedGameAction() != null) {
            makeKeyAction(ROBOT::keyRelease, false, true, true);
            GameHistory.setNotReleasedGameAction(null);
        }
        handleControls(ROBOT::keyPress, true, false);
        handleControls(ROBOT::keyRelease, false, true);
    }

    private void handleControls(IntConsumer keyAction, boolean needDelay, boolean reverse) {
        if (!needDelay && ControlsEnum.movingControlsSet().containsAll(gameAction.getControls())) {
            GameHistory.setNotReleasedGameAction(gameAction);
            return;
        }
        makeKeyAction(keyAction, needDelay, reverse, false);
    }

    private void makeKeyAction(IntConsumer keyAction, boolean needDelay, boolean isReverse, boolean isFromHistory) {
        List<ControlsEnum> controls = isFromHistory
                ? GameHistory.getNotReleasedGameAction().getControls() : gameAction.getControls();
        if (isReverse) {
            controls = new ArrayList<>(controls);
            Collections.reverse(controls);
        }
        controls.forEach(control -> {
            List<Integer> keyEvents = CONTROLS_ENUM_KEY_CODE_MAP.get(control);
            keyEvents.forEach(keyAction::accept);
            if (needDelay || (isReverse && ControlsEnum.movingControlsSet().contains(control))) {
                ROBOT.delay(control.getDelay().get());
            }
        });
    }
}
