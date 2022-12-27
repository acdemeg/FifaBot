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
    private final int[][] pixels;

    public ImageAnalysis(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        Comparator<Point> comparator = Comparator.comparingDouble(Point::getY)
                .thenComparingDouble(Point::getX);
        this.playmates = new TreeSet<>(comparator);
        this.opposites = new TreeSet<>(comparator);
        this.pixels = new int[width][height];
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
                pixels[x][y] = pixel;

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
        searchOverlayPlayers();

        return new GameInfo(playmates, opposites, activePlayer, ball, pixels);
    }

    private void searchOverlayPlayers() {
        boolean playersNotEnough = opposites.size() < 11 || playmates.size() < 11;

        if (ball != null && playersNotEnough) {
            // looking in the vicinity of the ball
            searchOverlayPlayersBase(ball, true, false, false);
        }
        if (playersNotEnough) {
            // if playmate on opposite or vice versa
            searchOtherPlayerOverlay();
        }

    }

    private void searchOtherPlayerOverlay() {
        if (playmates.size() < 11) {
            opposites.forEach(opponent -> searchOverlayPlayersBase(opponent, false, true, false));
        }
        else if (opposites.size() < 11) {
            playmates.forEach(playmate -> searchOverlayPlayersBase(playmate, false, false, true));
        }
    }

    private void searchOverlayPlayersBase(Point point, boolean isBall, boolean isPlaymate, boolean isOpposite) {
        Point leftTopScanPoint = new Point(point.x - 5, point.y - 5);
        Point bottomRightScanPoint = new Point(point.x + 5, point.y + 5);

        if(leftTopScanPoint.x >= 0 && leftTopScanPoint.y >= 0
                && bottomRightScanPoint.x < width && bottomRightScanPoint.y < height) {

            for (int y = leftTopScanPoint.y; y <= bottomRightScanPoint.y; y++) {
                for (int x = leftTopScanPoint.x; x <= bottomRightScanPoint.x; x++) {
                    int pixel = pixels[x][y];
                    int finalX = x;
                    int finalY = y;
                    if ( (isOpposite || isBall) && opposites.size() < 11 &&
                            (isOverlayOppositePlayerColor(pixel) || isPlayerColor(pixel, false))) {
                        boolean isExistPoint = opposites.stream().anyMatch(p -> p.distance(finalX, finalY) < 5);
                        if (!isExistPoint) {
                            opposites.add(new Point(x, y));
                        }
                    }
                    if ( (isPlaymate || isBall) && playmates.size() < 11 &&
                            (isOverlayPlaymatePlayerColor(pixel) || isPlayerColor(pixel, true))) {
                        boolean isExistPoint = playmates.stream().anyMatch(p -> p.distance(finalX, finalY) < 5);
                        if (!isExistPoint) {
                            playmates.add(new Point(x, y));
                        }
                    }
                }
            }
        }
    }

    private int addPlayer(int x, int y, Function<Integer, Boolean> isBoundColor, boolean isActivePlayer) {
        int endPlayerBound = getEndPlayerBound(x + 1, y, isBoundColor);
        int middlePlayerBound = endPlayerBound - ( (endPlayerBound - x) / 2);
        boolean isAdd = setPlayerCoordinate(middlePlayerBound, y + 4, isActivePlayer)
                        || setPlayerCoordinate(middlePlayerBound, y - 4, isActivePlayer)
                        || setPlayerCoordinate(middlePlayerBound, y + 3, isActivePlayer)
                        || setPlayerCoordinate(middlePlayerBound, y - 3, isActivePlayer)
                        || setPlayerCoordinate(middlePlayerBound, y + 2, isActivePlayer)
                        || setPlayerCoordinate(middlePlayerBound, y - 2, isActivePlayer)
                        || setPlayerCoordinate(middlePlayerBound, y + 1, isActivePlayer)
                        || setPlayerCoordinate(middlePlayerBound, y - 1, isActivePlayer);
        return isAdd ? endPlayerBound : x;
    }

    private boolean isExistBottomRightNearPoint(int x, int y) {
        if (y + 4 < height) {
            int pixel = bufferedImage.getRGB(x, y + 4);
            boolean isPlaymate = isPlayerColor(pixel, true);
            Stream<Point> players = isPlaymate ? playmates.stream() : opposites.stream();
            Predicate<Point> pointNear = p -> (p.x >= x && p.x - x < 6) && (p.y >= y && p.y - y < 9);
            return players.anyMatch(pointNear);
        }
        return false;
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
        if (y > 0 && y < height) {

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

    private boolean isOverlayOppositePlayerColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        return overlayOppositePlayerColorLower.getRed() < r && overlayOppositePlayerColorUpper.getRed() > r
                && overlayOppositePlayerColorLower.getGreen() < g && overlayOppositePlayerColorUpper.getGreen() > g
                && overlayOppositePlayerColorLower.getBlue() < b && overlayOppositePlayerColorUpper.getBlue() > b;
    }

    private boolean isOverlayPlaymatePlayerColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        return overlayPlaymatePlayerColorLower.getRed() < r && overlayPlaymatePlayerColorUpper.getRed() > r
                && overlayPlaymatePlayerColorLower.getGreen() < g && overlayPlaymatePlayerColorUpper.getGreen() > g
                && overlayPlaymatePlayerColorLower.getBlue() > b && overlayPlaymatePlayerColorUpper.getBlue() < b;
    }
}
