package org.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.awt.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeometryUtils {

    public static Rectangle getSquareBetweenPlayers(Point player1, Point player2) {
        Point upperLeft = new Point(Math.min(player1.x, player2.x), Math.min(player1.y, player2.y));
        Point bottomRight = new Point(Math.max(player1.x, player2.x),Math.max(player1.y, player2.y));
        Dimension squareDimension = new Dimension(bottomRight.x - upperLeft.x,bottomRight.y - upperLeft.y);
        return new Rectangle(upperLeft, squareDimension);
    }
}
