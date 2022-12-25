package org.example;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.example.GameInfo.*;


public class ImageAnalysis {

    private final BufferedImage bufferedImage;
    private final SortedSet<Point> playmates;
    private final SortedSet<Point> opposites;
    private Point activePlayer;
    private Point ball;

    public ImageAnalysis(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        Comparator<Point> comparator = Comparator.comparingDouble(Point::getY)
                .thenComparingDouble(Point::getX);
        this.playmates = new TreeSet<>(comparator);
        this.opposites = new TreeSet<>(comparator);
    }

    /**
     * Concept:<p>
     * 1-Step: Iterating by {@code bufferedImage} rectangle top-down and left right straight
     *  and get pixel integer color with x,y coordinates. <p>
     * 2-Step: Check whether pixel represent bound of player.<p>
     * 3-Step: Check whether exist Point which can be center coordinates of Player
     *  and located below and to the right then given (x,y), if Yes - current {@code x} replace {@code  endPlayerBound}
     *  and continue for cycle, if No - starting 4 Step.<p>
     * 4-Step: Find middle of bound Player and trying to add in Set new Player via {@code setPlayerCoordinate} method.<p>
     * 5-Step: If already exist point which locate in radius = 8 from given (x, y) point then set of players not modify,
     *  else add new coordinate in corresponding Set in depend on player belong to.<p>
     */
    public GameInfo analyse() {

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int pixel = bufferedImage.getRGB(x, y);

                if (isBoundPlayerColor(pixel)) {

                    if (isExistBottomRightNearPoint(x, y)) {
                        x = getEndPlayerBound(x + 1, y, this::isBoundPlayerColor);
                    }
                    else {
                        x = addPlayer(x, y, this::isBoundPlayerColor, false);
                    }
                }
                else if (activePlayer == null && isActivePlayerColor(pixel)) {
                    x = addPlayer(x, y, this::isActivePlayerColor, true);
                }
                else if (ball == null && isBallColor(pixel)) {
                    ball = new Point(x + 1, y + 5);
                }
            }
        }

        return new GameInfo(playmates, opposites, activePlayer, ball);
    }

    private int addPlayer(int x, int y, Function<Integer, Boolean> isBoundColor, boolean isActivePlayer) {
        int endPlayerBound = getEndPlayerBound(x + 1, y, isBoundColor);
        int middlePlayerBound = endPlayerBound - ( (endPlayerBound - x) / 2);
        boolean isAdd = setPlayerCoordinate(middlePlayerBound, y + 4, isActivePlayer);
        return isAdd ? endPlayerBound : x;
    }

    private boolean isExistBottomRightNearPoint(int x, int y) {
        Stream<Point> players = Stream.concat(playmates.stream(), opposites.stream());
        Predicate<Point> pointNear = p -> (p.x >= x && p.x - x < 6) && (p.y >= y && p.y - y < 9);
        return players.anyMatch(pointNear);
    }

    private int getEndPlayerBound(int x, int y, Function<Integer, Boolean> isBoundColor) {
        if (x < width) {
            int pixel = bufferedImage.getRGB(x, y);
            if (isBoundColor.apply(pixel)) {
                return getEndPlayerBound(x + 1, y, isBoundColor);
            }
            return x - 1;
        }
        return x - 1;
    }

    private boolean setPlayerCoordinate(int x, int y, boolean isActivePlayer) {
        if (y < height) {

            if (isActivePlayer) {
                activePlayer = new Point(x, y);
                return playmates.add(activePlayer);
            }

            int pixel = bufferedImage.getRGB(x, y);

            if (isPlayerColor(pixel, true)) {
                boolean isExistPoint = playmates.stream().anyMatch(point -> point.distance(x, y) < 8);
                return isExistPoint || playmates.add(new Point(x, y));
            }
            else if (isPlayerColor(pixel, false)) {
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

    private boolean isPlayerColor(int pixel, boolean isPlaymate) {
        Color playerColorLower;
        Color playerColorUpper;
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        if(isPlaymate){
            playerColorLower = playmateColorLower;
            playerColorUpper = playmateColorUpper;
        }
        else {
            playerColorLower = oppositeColorLower;
            playerColorUpper = oppositeColorUpper;
        }

        return playerColorLower.getRed() < r && playerColorUpper.getRed() > r
                && playerColorLower.getGreen() < g && playerColorUpper.getGreen() > g
                && playerColorLower.getBlue() < b && playerColorUpper.getBlue() > b;
    }

    private boolean isActivePlayerColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        return activePlayerLower.getRed() > r && activePlayerUpper.getRed() < r
                && activePlayerLower.getGreen() < g && activePlayerUpper.getGreen() > g
                && activePlayerLower.getBlue() < b && activePlayerUpper.getBlue() > b
                && Math.abs(g - b) < 11 && Math.abs(r - g) > 100;
    }

    private boolean isBallColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        return ballColorLower.getRed() < r && ballColorUpper.getRed() > r
                && ballColorLower.getGreen() < g && ballColorUpper.getGreen() > g
                && ballColorLower.getBlue() >= b && ballColorUpper.getBlue() <= b;
    }

    public void pixelLogging(int pixel, int x, int y, Color color) {
        System.out.println(
                "Pixel RGB = " + pixel + ", " +
                "Position = [" + x + "," + y + "], " +
                "Color(r,g,b) = [" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + "]"
        );
    }
}
