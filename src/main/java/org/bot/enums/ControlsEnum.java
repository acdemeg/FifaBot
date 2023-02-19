package org.bot.enums;

import lombok.Getter;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This enum represent game actions controls
 * for 'Keyboard only' control type.
 * Here list for Attack and Defence actions
 */
public enum ControlsEnum {
    // One key actions
    SPRINT(new AtomicInteger(50)),
    MOVE_UP(new AtomicInteger(50)),
    MOVE_DOWN(new AtomicInteger(50)),
    MOVE_LEFT(new AtomicInteger(50)),
    MOVE_RIGHT(new AtomicInteger(50)),
    MOVE_UP_PAD(new AtomicInteger(50)),
    MOVE_DOWN_PAD(new AtomicInteger(50)),
    MOVE_LEFT_PAD(new AtomicInteger(50)),
    MOVE_RIGHT_PAD(new AtomicInteger(50)),
    TACTICS(new AtomicInteger(50)),
    MENTALITY_LEFT(new AtomicInteger(50)),
    MENTALITY_RIGHT(new AtomicInteger(50)),
    CUSTOM_TACTICS(new AtomicInteger(50)),
    SWITCH_FROM_GK(new AtomicInteger(50)),
    ATTACK_THROUGH_BALL(new AtomicInteger(50)),
    ATTACK_LOB_PASS_CROSS_HEADER(new AtomicInteger(50)),
    ATTACK_SHOOT_VOLLEY_HEADER(new AtomicInteger(50)),
    ATTACK_SHORT_PASS_HEADER(new AtomicInteger(50)),
    ATTACK_PLAYER_RUN_MODIFIER(new AtomicInteger(50)),
    ATTACK_FINESSE_SHOT_MODIFIER(new AtomicInteger(50)),
    ATTACK_PROTECT_BALL(new AtomicInteger(50)),
    DEFENCE_RUSH_GK(new AtomicInteger(50)),
    DEFENCE_SLIDING_TACKLE(new AtomicInteger(50)),
    DEFENCE_TACKLE_PUSH_OR_PULL(new AtomicInteger(50)),
    DEFENCE_CONTAIN(new AtomicInteger(50)),
    DEFENCE_CHANGE_PLAYER(new AtomicInteger(50)),
    DEFENCE_TEAMMATE_CONTAIN(new AtomicInteger(50)),
    DEFENCE_JOCKEY(new AtomicInteger(50)),
    // multi-key actions
    CHIP_SHOT(new AtomicInteger(50)),
    FINESSE_SHOT(new AtomicInteger(50)),
    LOW_SHOT(new AtomicInteger(50)),
    THREADED_THROUGH_PASS(new AtomicInteger(50)),
    LOBBED_THROUGH_PASS(new AtomicInteger(50)),
    DRIVEN_LOB_PASS_CROSS(new AtomicInteger(50)),
    HIGH_LOB_CROSS(new AtomicInteger(50)),
    LOW_CROSS(new AtomicInteger(50)),
    SCOOP_LOB(new AtomicInteger(50)),
    CANCEL(new AtomicInteger(50)),
    FLAIR_PASS(new AtomicInteger(50)),
    FLAIR_SHOOT(new AtomicInteger(50)),
    DRIVEN_GROUND_PASS(new AtomicInteger(50)),
    RUNNING_JOCKEY(new AtomicInteger(50)),
    // if impossible decide
    NONE(new AtomicInteger());

    @Getter
    private final AtomicInteger delay;

    public static Set<ControlsEnum> movingControlsSet() {
        return Set.of(SPRINT, ATTACK_PROTECT_BALL, MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT);
    }

    public static Set<ControlsEnum> shotControlsSet() {
        return Set.of(ATTACK_SHOOT_VOLLEY_HEADER, CHIP_SHOT, LOW_SHOT, FINESSE_SHOT, FLAIR_SHOOT);
    }

    public static Set<ControlsEnum> passiveControlsSet() {
        return Set.of(NONE, ATTACK_PROTECT_BALL);
    }

    ControlsEnum(AtomicInteger delay) {
        this.delay = delay;
    }

}
