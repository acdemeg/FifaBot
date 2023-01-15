package org.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.awt.*;

/**
 * This class provides utils methods for base 2d geometry over {@code Point} objects
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeometryUtils {

    /**
     * This method build {@code Rectangle} by two {@code Point}
     * @param player1 first player
     * @param player2 second player
     * @return Rectangle area which bases on two points
     */
    public static Rectangle getRectangleBetweenPlayers(Point player1, Point player2) {
        Point upperLeft = new Point(Math.min(player1.x, player2.x), Math.min(player1.y, player2.y));
        Point bottomRight = new Point(Math.max(player1.x, player2.x),Math.max(player1.y, player2.y));
        Dimension dimension = new Dimension(bottomRight.x - upperLeft.x,bottomRight.y - upperLeft.y);
        return new Rectangle(upperLeft, dimension);
    }

    /**
     * This method calculate height for random triangle
     * @param player1 first player
     * @param player2 second player
     * @param player3 third player
     * @return Height of triangle drawn to side between points player1 and player2
     */
    public static double calculateTriangleHeight(Point player1, Point player2, Point player3) {
        double a = player1.distance(player2);
        double b = player2.distance(player3);
        double c = player1.distance(player3);
        double semiPerimeter = (a + b + c) / 2;
        return (2 / a) * Math.sqrt(semiPerimeter * (semiPerimeter - a) * (semiPerimeter - b) * (semiPerimeter - c));
    }

    /**
     * This method define shot direction
     * @param shotCandidate shot target player
     * @param activePlayer  active player
     * @param playmateSide  field side of playmates
     * @return shot direction as {@code GeomEnum}
     */
    public static GeomEnum defineShotDirection(Point shotCandidate, Point activePlayer, GameConstantsEnum playmateSide,
                                               double adjacentSide, double hypotenuse) {
        GeomEnum direction;
        double angle = Math.acos(adjacentSide / hypotenuse);
        if ((activePlayer.x < shotCandidate.x) && (activePlayer.y < shotCandidate.y)) {
            direction = getDirection(angle, GeomEnum.RIGHT, GeomEnum.BOTTOM, GeomEnum.BOTTOM_RIGHT);
        }
        else if ((activePlayer.x < shotCandidate.x) && (activePlayer.y > shotCandidate.y)) {
            direction = getDirection(angle, GeomEnum.RIGHT, GeomEnum.TOP, GeomEnum.TOP_RIGHT);
        }
        else if ((activePlayer.x > shotCandidate.x) && (activePlayer.y > shotCandidate.y)) {
            direction = getDirection(angle, GeomEnum.LEFT, GeomEnum.TOP, GeomEnum.TOP_LEFT);
        }
        else if ((activePlayer.x > shotCandidate.x) && (activePlayer.y < shotCandidate.y)) {
            direction = getDirection(angle, GeomEnum.LEFT, GeomEnum.BOTTOM, GeomEnum.BOTTOM_LEFT);
        }
        else {
            direction = playmateSide.equals(GameConstantsEnum.LEFT_PLAYMATE_SIDE) ? GeomEnum.LEFT : GeomEnum.RIGHT;
        }

        return direction;
    }

    private static GeomEnum getDirection(double angle, GeomEnum horizontalDirection,
                                         GeomEnum verticalDirection, GeomEnum direction2D) {
        if (angle < Math.PI / 12) {
            return horizontalDirection;
        }
        else if (angle > (5 * Math.PI) / 12) {
            return verticalDirection;
        }
        return direction2D;
    }
}
