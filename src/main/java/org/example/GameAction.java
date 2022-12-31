package org.example;

public class GameAction {

    private final int delay;

    private final ControlsEnum control;

    public GameAction(int delay, ControlsEnum control) {
        this.delay = delay;
        this.control = control;
    }

}
