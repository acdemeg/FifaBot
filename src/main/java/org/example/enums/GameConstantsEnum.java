package org.example.enums;

import lombok.Getter;

import java.awt.*;

import static org.example.GameInfo.HEIGHT;
import static org.example.GameInfo.WIDTH;

/**
 * Common game constants enum
 */
public enum GameConstantsEnum {
    LEFT_PLAYMATE_SIDE(new Point(0, 0)),
    RIGHT_PLAYMATE_SIDE(new Point(WIDTH, HEIGHT)),
    CENTER_FIELD_POINT(new Point(129, 77)),
    LEFT_PENALTY_POINT(new Point(33, 77)),
    RIGHT_PENALTY_POINT(new Point(225, 77)),
    LEFT_PENALTY_AREA_TOP_POINT(new Point(48, 31)),
    LEFT_PENALTY_AREA_BOTTOM_POINT(new Point(48, 124)),
    RIGHT_PENALTY_AREA_TOP_POINT(new Point(208, 31)),
    RIGHT_PENALTY_AREA_BOTTOM_POINT(new Point(208, 124)),
    LEFT_GOALKEEPER_AREA_TOP_POINT(new Point(16, 55)),
    LEFT_GOALKEEPER_AREA_BOTTOM_POINT(new Point(16, 99)),
    RIGHT_GOALKEEPER_AREA_TOP_POINT(new Point(40, 55)),
    RIGHT_GOALKEEPER_AREA_BOTTOM_POINT(new Point(240, 99)),
    LEFT_PENALTY_AREA(new Rectangle(
            0, LEFT_PENALTY_AREA_TOP_POINT.getPoint().y, LEFT_PENALTY_AREA_TOP_POINT.getPoint().x,
            LEFT_PENALTY_AREA_BOTTOM_POINT.getPoint().y - LEFT_PENALTY_AREA_TOP_POINT.getPoint().y
    )),
    RIGHT_PENALTY_AREA(new Rectangle(
            RIGHT_PENALTY_AREA_TOP_POINT.getPoint().x, RIGHT_PENALTY_AREA_TOP_POINT.getPoint().y,
            WIDTH - RIGHT_PENALTY_AREA_TOP_POINT.getPoint().x,
            RIGHT_PENALTY_AREA_BOTTOM_POINT.getPoint().y - RIGHT_PENALTY_AREA_TOP_POINT.getPoint().y
    ));

    @Getter
    private Point point;
    @Getter
    private Rectangle rectangle;

    GameConstantsEnum(Point point) {
        this.point = point;
    }

    GameConstantsEnum(Rectangle rectangle) {
        this.rectangle = rectangle;
    }
}
