package org.example;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.IntConsumer;

import static org.example.GameAction.CONTROLS_ENUM_KEY_CODE_MAP;
import static org.example.Main.ROBOT;

@RequiredArgsConstructor
public class ActionProducer {

    private final GameAction gameAction;

    public void makeGameAction() {
        handleControls(ROBOT::keyPress);
        handleControls(ROBOT::keyRelease);
    }

    private void handleControls(IntConsumer keyAction) {
        gameAction.getControls().forEach(control -> {
            List<Integer> keyEvents = CONTROLS_ENUM_KEY_CODE_MAP.get(control);
            keyEvents.forEach(keyAction::accept);
            ROBOT.delay(control.getDelay().get());
        });
    }
}
