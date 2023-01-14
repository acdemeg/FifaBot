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
}
