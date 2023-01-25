package org.example.enums;

import lombok.Getter;

import java.awt.*;

/**
 * Enum all colors for {@link  org.example.ImageAnalysis}
 */
public enum ColorsEnum {
    PLAYMATE_COLOR_LOWER(new Color(100, 20, 20)),
    PLAYMATE_COLOR_UPPER(new Color(255, 0, 0)),
    OPPOSITE_COLOR_LOWER(new Color(35, 109, 170)),
    OPPOSITE_COLOR_UPPER(new Color(0, 0, 255)),
    ACTIVE_PLAYER_LOWER(new Color(25, 100, 100)),
    ACTIVE_PLAYER_UPPER(new Color(0, 210, 210)),
    BOUND_OF_PLAYER_COLOR(new Color(222, 222, 222)),
    OVERLAY_BOUND_OF_PLAYER_COLOR(new Color(130, 130, 130)),
    BALL_COLOR_LOWER(new Color(180, 135, 1)),
    BALL_COLOR_UPPER(new Color(255, 186, 0)),
    OVERLAY_OPPOSITE_PLAYER_COLOR_LOWER(new Color(21, 25, 40)),
    OVERLAY_OPPOSITE_PLAYER_COLOR_UPPER(new Color(36, 45, 60)),
    OVERLAY_PLAYMATE_PLAYER_COLOR_LOWER(new Color(100, 45, 16)),
    OVERLAY_PLAYMATE_PLAYER_COLOR_UPPER(new Color(115, 60, 8)),
    SHADING_FIELD_COLOR_LOWER(new Color(60, 100, 20)),
    SHADING_FIELD_COLOR_UPPER(new Color(110, 140, 80));

    @Getter
    private final Color color;

    ColorsEnum(Color color) {
        this.color = color;
    }
}
