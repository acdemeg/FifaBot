package org.example;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.example.GameInfo.*;


public class ImageAnalysis {

    private final BufferedImage bufferedImage;
    private final SortedSet<Point> playmates;
    private final SortedSet<Point> opposites;

    public ImageAnalysis(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        Comparator<Point> comparator = Comparator.comparingDouble(Point::getY)
                .thenComparingDouble(Point::getX);
        this.playmates = new TreeSet<>(comparator);
        this.opposites = new TreeSet<>(comparator);
    }

    /**
     * Concept:
     * 1-Step: Iterating by {@code bufferedImage} rectangle top-down and left right straight
     *  and get pixel integer color with x,y coordinates.
     * 2-Step: Check whether pixel represent bound of player.
     * 3-Step: Check whether exist Point which can be center coordinates of Player
     *  and located below and to the right then given (x,y), if Yes - current {@code x} replace {@code  endPlayerBound}
     *  and continue for cycle, if No - starting 4 Step.
     * 4-Step: Find middle of bound Player and trying to add in Set new Player via {@code setPlayerCoordinate} method.
     * 5-Step: If already exist point which locate in radius = 8 from given (x, y) point then set of players not modify,
     *  else add new coordinate in corresponding Set in depend on player belong to.
     */
    public GameInfo analyse() {

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int pixel = bufferedImage.getRGB(x, y);

                if (isBoundPlayerColor(pixel)) {

                    if (isExistBottomRightNearPoint(x, y)) {
                        x = getEndPlayerBound(x + 1, y);
                    }
                    else {
                        int endPlayerBound = getEndPlayerBound(x + 1, y);
                        int middlePlayerBound = endPlayerBound - ( (endPlayerBound - x) / 2);
                        boolean isAdd = setPlayerCoordinate(middlePlayerBound, y + 4);
                        x = isAdd ? endPlayerBound : x;
                    }
                }
            }
        }

        return new GameInfo(playmates, opposites);
    }

    private boolean isExistBottomRightNearPoint(int x, int y) {
        Stream<Point> players = Stream.concat(playmates.stream(), opposites.stream());
        Predicate<Point> pointNear = p -> (p.x >= x && p.x - x < 6) && (p.y >= y && p.y - y < 9);
        return players.anyMatch(pointNear);
    }

    private int getEndPlayerBound(int x, int y) {
        if (x < width) {
            int pixel = bufferedImage.getRGB(x, y);
            if (isBoundPlayerColor(pixel)) {
                return getEndPlayerBound(x + 1, y);
            }
            return x - 1;
        }
        return x - 1;
    }

    private boolean setPlayerCoordinate(int x, int y) {
        if (y < height) {
            int pixel = bufferedImage.getRGB(x, y);

            if (pixel == playmateColor) {
                boolean isExistPoint = playmates.stream().anyMatch(point -> point.distance(x, y) < 8);
                return isExistPoint || playmates.add(new Point(x, y));
            }
            else if (pixel == oppositeColor) {
                boolean isExistPoint = opposites.stream().anyMatch(point -> point.distance(x, y) < 8);
                return isExistPoint || opposites.add(new Point(x, y));
            }
            return false;
        }
        return false;
    }

    private boolean isBoundPlayerColor(int pixel) {
        return boundOfPlayerColor.getRed() < ((pixel >> 16) & 0xFF)
                && boundOfPlayerColor.getGreen() < ((pixel >> 8) & 0xFF)
                && boundOfPlayerColor.getBlue() < (pixel & 0xFF);
    }

    public void pixelLogging(int pixel, int x, int y, Color color) {
        System.out.println(
                "Pixel RGB = " + pixel + ", " +
                "Position = [" + x + "," + y + "], " +
                "Color(r,g,b) = [" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + "]"
        );
    }
}
