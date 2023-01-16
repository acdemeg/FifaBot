package org.example;

import lombok.Getter;

import java.util.List;

import static org.example.ControlsEnum.*;

/**
 * Geometric enum for internal using
 */
public enum GeomEnum {
    BOTTOM_RIGHT(List.of(MOVE_DOWN, MOVE_RIGHT)),
    TOP_RIGHT(List.of(MOVE_UP, MOVE_RIGHT)),
    BOTTOM_LEFT(List.of(MOVE_DOWN, MOVE_LEFT)),
    TOP_LEFT(List.of(MOVE_UP, MOVE_LEFT)),
    BOTTOM(List.of(MOVE_DOWN)),
    TOP(List.of(MOVE_UP)),
    LEFT(List.of(MOVE_LEFT)),
    RIGHT(List.of(MOVE_RIGHT));

    @Getter
    private final List<ControlsEnum> controlsList;

    GeomEnum(List<ControlsEnum> controlsEnumList) {
        this.controlsList = controlsEnumList;
    }
}
