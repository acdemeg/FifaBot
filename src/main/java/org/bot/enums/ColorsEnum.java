package org.bot.enums;

import lombok.Getter;

import java.awt.*;

/**
 * Enum all colors for {@link  org.bot.ImageAnalysis}
 */
public enum ColorsEnum {
    PLAYMATE_COLOR_LOWER(new Color(100, 35, 30)),
    PLAYMATE_COLOR_UPPER(new Color(255, 0, 0)),
    OPPOSITE_COLOR_LOWER(new Color(35, 109, 170)),
    OPPOSITE_COLOR_UPPER(new Color(0, 0, 255)),
    ACTIVE_PLAYER_LOWER(new Color(50, 100, 100)),
    ACTIVE_PLAYER_UPPER(new Color(0, 210, 210)),
    BOUND_OF_PLAYER_COLOR_LOWER(new Color(170, 170, 170)),
    BOUND_OF_PLAYER_COLOR_UPPER(new Color(255, 255, 255)),
    BALL_COLOR_LOWER(new Color(180, 135, 60)),
    BALL_COLOR_UPPER(new Color(255, 186, 0)),
    SHADING_PLAYMATE_COLOR_LOWER(new Color(100, 0, 0)),
    SHADING_PLAYMATE_COLOR_UPPER(new Color(255, 100, 80)),
    SHADING_OPPOSITE_COLOR_LOWER(new Color(0, 0, 70)),
    SHADING_OPPOSITE_COLOR_UPPER(new Color(70, 120, 255)),
    SHADING_ACTIVE_PLAYER_LOWER(new Color(0, 100, 80)),
    SHADING_ACTIVE_PLAYER_UPPER(new Color(70, 210, 255)),
    SHADING_BOUND_OF_PLAYER_COLOR_LOWER(new Color(100, 80, 40)),
    SHADING_BALL_COLOR_LOWER(new Color(90, 100, 0)),
    SHADING_BALL_COLOR_UPPER(new Color(255, 200, 30)),
    OVERLAY_BOUND_OF_PLAYER_COLOR(new Color(130, 130, 130)),
    OVERLAY_OPPOSITE_PLAYER_COLOR_LOWER(new Color(0, 25, 110)),
    OVERLAY_OPPOSITE_PLAYER_COLOR_UPPER(new Color(36, 80, 170)),
    OVERLAY_PLAYMATE_PLAYER_COLOR_LOWER(new Color(100, 45, 16)),
    OVERLAY_PLAYMATE_PLAYER_COLOR_UPPER(new Color(115, 60, 8)),
    SHADING_FIELD_COLOR_LOWER(new Color(60, 100, 20)),
    SHADING_FIELD_COLOR_UPPER(new Color(120, 140, 80));

    @Getter
    private final Color color;

    ColorsEnum(Color color) {
        this.color = color;
    }
}
