package org.example.enums;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This enum represent game actions controls
 * for 'Keyboard only' control type.
 * Here list for Attack and Defence actions
 */
public enum ControlsEnum {
    // One key actions
    SPRINT(new AtomicInteger(5), 40),
    MOVE_UP(new AtomicInteger(5), 50),
    MOVE_DOWN(new AtomicInteger(5), 50),
    MOVE_LEFT(new AtomicInteger(5), 50),
    MOVE_RIGHT(new AtomicInteger(5), 50),
    MOVE_UP_PAD(new AtomicInteger(5), 0),
    MOVE_DOWN_PAD(new AtomicInteger(5), 0),
    MOVE_LEFT_PAD(new AtomicInteger(5), 0),
    MOVE_RIGHT_PAD(new AtomicInteger(5), 0),
    TACTICS(new AtomicInteger(5), 0),
    MENTALITY_LEFT(new AtomicInteger(5), 0),
    MENTALITY_RIGHT(new AtomicInteger(5), 0),
    CUSTOM_TACTICS(new AtomicInteger(5), 0),
    SWITCH_FROM_GK(new AtomicInteger(5), 0),
    ATTACK_THROUGH_BALL(new AtomicInteger(5), 0),
    ATTACK_LOB_PASS_CROSS_HEADER(new AtomicInteger(5), 0),
    ATTACK_SHOOT_VOLLEY_HEADER(new AtomicInteger(5), 10),
    ATTACK_SHORT_PASS_HEADER(new AtomicInteger(5), 20),
    ATTACK_PLAYER_RUN_MODIFIER(new AtomicInteger(5), 0),
    ATTACK_FINESSE_SHOT_MODIFIER(new AtomicInteger(5), 0),
    ATTACK_PROTECT_BALL(new AtomicInteger(5), 90),
    DEFENCE_RUSH_GK(new AtomicInteger(5), 0),
    DEFENCE_SLIDING_TACKLE(new AtomicInteger(5), 0),
    DEFENCE_TACKLE_PUSH_OR_PULL(new AtomicInteger(5), 0),
    DEFENCE_CONTAIN(new AtomicInteger(5), 0),
    DEFENCE_CHANGE_PLAYER(new AtomicInteger(5), 0),
    DEFENCE_TEAMMATE_CONTAIN(new AtomicInteger(5), 0),
    DEFENCE_JOCKEY(new AtomicInteger(5), 0),
    // multi-key actions
    CHIP_SHOT(new AtomicInteger(5), 0),
    FINESSE_SHOT(new AtomicInteger(5), 0),
    LOW_SHOT(new AtomicInteger(5), 0),
    THREADED_THROUGH_PASS(new AtomicInteger(5), 0),
    LOBBED_THROUGH_PASS(new AtomicInteger(5), 0),
    DRIVEN_LOB_PASS_CROSS(new AtomicInteger(5), 0),
    HIGH_LOB_CROSS(new AtomicInteger(5), 0),
    LOW_CROSS(new AtomicInteger(5), 0),
    SCOOP_LOB(new AtomicInteger(5), 0),
    CANCEL(new AtomicInteger(5), 0),
    FLAIR_PASS(new AtomicInteger(5), 0),
    FLAIR_SHOOT(new AtomicInteger(5), 0),
    DRIVEN_GROUND_PASS(new AtomicInteger(5), 0),
    RUNNING_JOCKEY(new AtomicInteger(5), 0),
    // if impossible decide
    NONE(new AtomicInteger(), 100);

    @Getter
    private final AtomicInteger delay;
    @Getter
    private final int priority;

    ControlsEnum(AtomicInteger delay, int priority) {
        this.delay = delay;
        this.priority = priority;
    }

}
