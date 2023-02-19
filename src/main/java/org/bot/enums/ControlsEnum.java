package org.bot.enums;

import lombok.Getter;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.bot.enums.GameConstantsEnum.INIT_DELAY;

/**
 * This enum represent game actions controls
 * for 'Keyboard only' control type.
 * Here list for Attack and Defence actions
 */
public enum ControlsEnum {
    // One key actions
    SPRINT(new AtomicInteger(INIT_DELAY.getValue())),
    MOVE_UP(new AtomicInteger(INIT_DELAY.getValue())),
    MOVE_DOWN(new AtomicInteger(INIT_DELAY.getValue())),
    MOVE_LEFT(new AtomicInteger(INIT_DELAY.getValue())),
    MOVE_RIGHT(new AtomicInteger(INIT_DELAY.getValue())),
    MOVE_UP_PAD(new AtomicInteger(INIT_DELAY.getValue())),
    MOVE_DOWN_PAD(new AtomicInteger(INIT_DELAY.getValue())),
    MOVE_LEFT_PAD(new AtomicInteger(INIT_DELAY.getValue())),
    MOVE_RIGHT_PAD(new AtomicInteger(INIT_DELAY.getValue())),
    TACTICS(new AtomicInteger(INIT_DELAY.getValue())),
    MENTALITY_LEFT(new AtomicInteger(INIT_DELAY.getValue())),
    MENTALITY_RIGHT(new AtomicInteger(INIT_DELAY.getValue())),
    CUSTOM_TACTICS(new AtomicInteger(INIT_DELAY.getValue())),
    SWITCH_FROM_GK(new AtomicInteger(INIT_DELAY.getValue())),
    ATTACK_THROUGH_BALL(new AtomicInteger(INIT_DELAY.getValue())),
    ATTACK_LOB_PASS_CROSS_HEADER(new AtomicInteger(INIT_DELAY.getValue())),
    ATTACK_SHOOT_VOLLEY_HEADER(new AtomicInteger(INIT_DELAY.getValue())),
    ATTACK_SHORT_PASS_HEADER(new AtomicInteger(INIT_DELAY.getValue())),
    ATTACK_PLAYER_RUN_MODIFIER(new AtomicInteger(INIT_DELAY.getValue())),
    ATTACK_FINESSE_SHOT_MODIFIER(new AtomicInteger(INIT_DELAY.getValue())),
    ATTACK_PROTECT_BALL(new AtomicInteger(INIT_DELAY.getValue())),
    DEFENCE_RUSH_GK(new AtomicInteger(INIT_DELAY.getValue())),
    DEFENCE_SLIDING_TACKLE(new AtomicInteger(INIT_DELAY.getValue())),
    DEFENCE_TACKLE_PUSH_OR_PULL(new AtomicInteger(INIT_DELAY.getValue())),
    DEFENCE_CONTAIN(new AtomicInteger(INIT_DELAY.getValue())),
    DEFENCE_CHANGE_PLAYER(new AtomicInteger(INIT_DELAY.getValue())),
    DEFENCE_TEAMMATE_CONTAIN(new AtomicInteger(INIT_DELAY.getValue())),
    DEFENCE_JOCKEY(new AtomicInteger(INIT_DELAY.getValue())),
    // multi-key actions
    CHIP_SHOT(new AtomicInteger(INIT_DELAY.getValue())),
    FINESSE_SHOT(new AtomicInteger(INIT_DELAY.getValue())),
    LOW_SHOT(new AtomicInteger(INIT_DELAY.getValue())),
    THREADED_THROUGH_PASS(new AtomicInteger(INIT_DELAY.getValue())),
    LOBBED_THROUGH_PASS(new AtomicInteger(INIT_DELAY.getValue())),
    DRIVEN_LOB_PASS_CROSS(new AtomicInteger(INIT_DELAY.getValue())),
    HIGH_LOB_CROSS(new AtomicInteger(INIT_DELAY.getValue())),
    LOW_CROSS(new AtomicInteger(INIT_DELAY.getValue())),
    SCOOP_LOB(new AtomicInteger(INIT_DELAY.getValue())),
    CANCEL(new AtomicInteger(INIT_DELAY.getValue())),
    FLAIR_PASS(new AtomicInteger(INIT_DELAY.getValue())),
    FLAIR_SHOOT(new AtomicInteger(INIT_DELAY.getValue())),
    DRIVEN_GROUND_PASS(new AtomicInteger(INIT_DELAY.getValue())),
    RUNNING_JOCKEY(new AtomicInteger(INIT_DELAY.getValue())),
    // if impossible decide
    NONE(new AtomicInteger());

    @Getter
    private final AtomicInteger delay;

    public static Set<ControlsEnum> movingControlsSet() {
        return Set.of(SPRINT, ATTACK_PROTECT_BALL, DEFENCE_CONTAIN, MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT);
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
