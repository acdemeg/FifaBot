package org.example;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActionProducer {

    private final GameAction gameAction;

    public void releaseGameAction() {
        // here will handle gameAction
    }
}
