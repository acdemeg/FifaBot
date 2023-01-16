package org.example;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.awt.event.KeyEvent.*;
import static org.example.ControlsEnum.*;

/**
 * This class represent in-game control actions
 */
@RequiredArgsConstructor
public class GameAction {

    public static final Map<ControlsEnum, List<Integer>> CONTROLS_ENUM_KEY_CODE_MAP;
    private final List<ControlsEnum> controls;
    private final int delay;

    static {

        CONTROLS_ENUM_KEY_CODE_MAP = Map.ofEntries(
                Map.entry(SPRINT, List.of(VK_E)),
                Map.entry(MOVE_UP, List.of(VK_UP)),
                Map.entry(MOVE_DOWN, List.of(VK_DOWN)),
                Map.entry(MOVE_LEFT, List.of(VK_LEFT)),
                Map.entry(MOVE_RIGHT, List.of(VK_RIGHT)),
                Map.entry(MOVE_UP_PAD, List.of(VK_NUMPAD8)),
                Map.entry(MOVE_DOWN_PAD, List.of(VK_NUMPAD2)),
                Map.entry(MOVE_LEFT_PAD, List.of(VK_NUMPAD4)),
                Map.entry(MOVE_RIGHT_PAD, List.of(VK_NUMPAD6)),
                Map.entry(TACTICS, List.of(VK_J)),
                Map.entry(MENTALITY_LEFT, List.of(VK_K)),
                Map.entry(MENTALITY_RIGHT, List.of(VK_L)),
                Map.entry(CUSTOM_TACTICS, List.of(VK_CAPS_LOCK)),
                Map.entry(SWITCH_FROM_GK, List.of(VK_F)),
                Map.entry(ATTACK_THROUGH_BALL, List.of(VK_A)),
                Map.entry(ATTACK_LOB_PASS_CROSS_HEADER, List.of(VK_S)),
                Map.entry(ATTACK_SHOOT_VOLLEY_HEADER, List.of(VK_W)),
                Map.entry(ATTACK_SHORT_PASS_HEADER, List.of(VK_D)),
                Map.entry(ATTACK_PLAYER_RUN_MODIFIER, List.of(VK_SPACE)),
                Map.entry(ATTACK_FINESSE_SHOT_MODIFIER, List.of(VK_Q)),
                Map.entry(ATTACK_PROTECT_BALL, List.of(VK_SHIFT)),
                Map.entry(DEFENCE_RUSH_GK, List.of(VK_A)),
                Map.entry(DEFENCE_SLIDING_TACKLE, List.of(VK_S)),
                Map.entry(DEFENCE_TACKLE_PUSH_OR_PULL, List.of(VK_W)),
                Map.entry(DEFENCE_CONTAIN, List.of(VK_D)),
                Map.entry(DEFENCE_CHANGE_PLAYER, List.of(VK_SPACE)),
                Map.entry(DEFENCE_TEAMMATE_CONTAIN, List.of(VK_Q)),
                Map.entry(DEFENCE_JOCKEY, List.of(VK_SHIFT)),
                Map.entry(CHIP_SHOT, List.of(VK_SPACE, VK_W)),
                Map.entry(FINESSE_SHOT, List.of(VK_Q, VK_W)),
                Map.entry(LOW_SHOT, List.of(VK_SPACE, VK_Q, VK_W)),
                Map.entry(THREADED_THROUGH_PASS, List.of(VK_Q, VK_A)),
                Map.entry(LOBBED_THROUGH_PASS, List.of(VK_SPACE, VK_A)),
                Map.entry(DRIVEN_LOB_PASS_CROSS, List.of(VK_Q, VK_S)),
                Map.entry(HIGH_LOB_CROSS, List.of(VK_SPACE, VK_S)),
                Map.entry(LOW_CROSS, List.of(VK_S, VK_S)),
                Map.entry(SCOOP_LOB, List.of(VK_SHIFT, VK_S)),
                Map.entry(CANCEL, List.of(VK_SHIFT, VK_E)),
                Map.entry(FLAIR_PASS, List.of(VK_SHIFT, VK_D)),
                Map.entry(FLAIR_SHOOT, List.of(VK_SHIFT, VK_W)),
                Map.entry(DRIVEN_GROUND_PASS, List.of(VK_Q, VK_D)),
                Map.entry(RUNNING_JOCKEY, List.of(VK_SHIFT, VK_E)),
                Map.entry(NONE, Collections.emptyList()));
    }

}
