package org.bot.enums;

import lombok.Getter;

import java.awt.*;

import static org.bot.GameInfo.HEIGHT;
import static org.bot.GameInfo.WIDTH;

/**
 * Common game constants enum
 */
@Getter
public enum GameConstantsEnum {
    PLAYER_DIAMETER(10),
    INIT_DELAY(100),
    LEFT_TOP_CORNER(new Point(0, 0)),
    LEFT_BOTTOM_CORNER(new Point(0, HEIGHT)),
    RIGHT_TOP_CORNER(new Point(WIDTH, 0)),
    RIGHT_BOTTOM_CORNER(new Point(WIDTH, HEIGHT)),
    LEFT_PLAYMATE_SIDE(LEFT_BOTTOM_CORNER.getPoint(), LEFT_TOP_CORNER.getPoint()),
    RIGHT_PLAYMATE_SIDE(RIGHT_BOTTOM_CORNER.getPoint(), RIGHT_TOP_CORNER.getPoint()),
    CENTER_FIELD_POINT(new Point(129, 77)),
    LEFT_PENALTY_POINT(new Point(33, 77)),
    RIGHT_PENALTY_POINT(new Point(225, 77)),
    LEFT_FOOTBALL_GOAL(new Point(0, 77)),
    RIGHT_FOOTBALL_GOAL(new Point(WIDTH, 77)),
    LEFT_PENALTY_AREA_TOP_POINT(new Point(48, 31)),
    LEFT_PENALTY_AREA_BOTTOM_POINT(new Point(48, 124)),
    RIGHT_PENALTY_AREA_TOP_POINT(new Point(208, 31)),
    RIGHT_PENALTY_AREA_BOTTOM_POINT(new Point(208, 124)),
    LEFT_GOALKEEPER_AREA_TOP_POINT(new Point(16, 55)),
    LEFT_GOALKEEPER_AREA_BOTTOM_POINT(new Point(16, 99)),
    RIGHT_GOALKEEPER_AREA_TOP_POINT(new Point(240, 55)),
    RIGHT_GOALKEEPER_AREA_BOTTOM_POINT(new Point(240, 99)),
    LEFT_PENALTY_AREA(
            new Rectangle(0, LEFT_PENALTY_AREA_TOP_POINT.getPoint().y, LEFT_PENALTY_AREA_TOP_POINT.getPoint().x,
                          LEFT_PENALTY_AREA_BOTTOM_POINT.getPoint().y - LEFT_PENALTY_AREA_TOP_POINT.getPoint().y)),
    RIGHT_PENALTY_AREA(
            new Rectangle(RIGHT_PENALTY_AREA_TOP_POINT.getPoint().x, RIGHT_PENALTY_AREA_TOP_POINT.getPoint().y,
                          WIDTH - RIGHT_PENALTY_AREA_TOP_POINT.getPoint().x,
                          RIGHT_PENALTY_AREA_BOTTOM_POINT.getPoint().y - RIGHT_PENALTY_AREA_TOP_POINT.getPoint().y));

    private int value;
    private Point point;
    private Point bottom;
    private Point top;
    private Rectangle rectangle;

    GameConstantsEnum(int value) {
        this.value = value;
    }

    GameConstantsEnum(Point point) {
        this.point = point;
    }
    GameConstantsEnum(Point bottom, Point top) {
        this.bottom = bottom;
        this.top = top;
    }

    GameConstantsEnum(Rectangle rectangle) {
        this.rectangle = rectangle;
    }
}
