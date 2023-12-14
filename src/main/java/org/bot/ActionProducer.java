package org.bot;

import lombok.extern.java.Log;
import org.bot.enums.ControlsEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;

import static org.bot.GameAction.CONTROLS_ENUM_KEY_CODE_MAP;
import static org.bot.Main.ROBOT;
import static org.bot.enums.ControlsEnum.CANCEL;

/**
 * This class take responsible for events generation.
 * Now is available only keyboard actions
 */
@Log
public record ActionProducer(GameAction gameAction) {

    public void makeGameAction() {
        if (GameHistory.getNotReleasedGameAction() != null && !GameHistory.isContinuousAction()) {
            makeKeyAction(ROBOT::keyRelease, false, true, true);
            GameHistory.setNotReleasedGameAction(null);
        }
        handleControls(ROBOT::keyPress, true, false);
        handleControls(ROBOT::keyRelease, false, true);
    }

    private void handleControls(IntConsumer keyAction, boolean needDelay, boolean reverse) {
        if (!needDelay && ControlsEnum.movingControlsSet().containsAll(gameAction.controls())) {
            GameHistory.setNotReleasedGameAction(gameAction);
            return;
        }
        makeKeyAction(keyAction, needDelay, reverse, false);
    }

    private void makeKeyAction(IntConsumer keyAction, boolean needDelay, boolean isReverse, boolean isFromHistory) {
        List<ControlsEnum> controls;
        if (isFromHistory) {
            controls = GameHistory.getNotReleasedGameAction().controls();
            log.info("NotReleasedGameAction: " + controls);
        }
        else {
            controls = gameAction.controls();
        }
        if (isReverse) {
            controls = new ArrayList<>(controls);
            Collections.reverse(controls);
            log.info("KeyRelease: " + controls);
        }
        else {
            if (GameHistory.isPossessionChangedOnTrue()) {
				// cancel prev action 
				List<Integer> cancelCodes = CONTROLS_ENUM_KEY_CODE_MAP.get(CANCEL);
				cancelCodes.forEach(ROBOT::keyPress);
				ROBOT.delay(CANCEL.getDelay().get()); 
				cancelCodes.forEach(ROBOT::keyRelease);
                log.info("CANCEL KeyPress !");
			}
            log.info("KeyPress: " + controls);
        }

        controls.forEach(control -> {
            List<Integer> keyEvents = CONTROLS_ENUM_KEY_CODE_MAP.get(control);
            keyEvents.forEach(keyAction::accept);
            if (needDelay || (isReverse && ControlsEnum.movingControlsSet().contains(control))) {
                ROBOT.delay(control.getDelay().get());
            }
        });
    }

    public static void releaseAll() {
        CONTROLS_ENUM_KEY_CODE_MAP.values().forEach(controls -> controls.forEach(ROBOT::keyRelease));
    }
}
