package org.bot;

import lombok.RequiredArgsConstructor;
import org.bot.enums.GameConstantsEnum;
import org.bot.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.bot.GameInfo.HEIGHT;
import static org.bot.GameInfo.WIDTH;
import static org.bot.enums.ColorsEnum.*;
import static org.bot.enums.GameConstantsEnum.*;

/**
 * This class performing base analysis of football field scheme image
 */
public class ImageAnalysis {

    private static final double SEARCH_RADIUS_NEARLY_PLAYER = 8;
    private static final int X_DISTANCE_NEARLY_PLAYER = 6;
    private static final int Y_DISTANCE_NEARLY_PLAYER = 9;
    private static final int OVERLAY_PLAYER_BASE_DISTANCE = 5;
    private static final int CORNER_POINT_DISTANCE = 25;
    private static final int BALL_POSSESSION_RADIUS = 10;

    private final BufferedImage bufferedImage;
    private final SortedSet<Point> playmates;
    private final SortedSet<Point> opposites;
    private Point activePlayer;
    private Point ball;
    private boolean isPlaymateBallPossession;
    private boolean isNobodyBallPossession;
    private boolean isShadingField;
    private boolean isCorner;
    private GameConstantsEnum playmateSide;
    private final int[][] pixels;

    @RequiredArgsConstructor
    private static final class SearchConditions {
        final Point point;
        final boolean isBall;
        final boolean isPlaymate;
        final boolean isOpposite;
        BaseData baseData;

        private record BaseData(int x, int y, int pixel) {
        }
    }

    public ImageAnalysis(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        Comparator<Point> comparator = Comparator.comparingDouble(Point::getY).thenComparingDouble(Point::getX);
        this.playmates = new TreeSet<>(comparator);
        this.opposites = new TreeSet<>(comparator);
        this.pixels = new int[WIDTH][HEIGHT];
    }

    /**
     * Concept:<p>
     * 1-Step: Iterating by {@code bufferedImage} rectangle top-down and left right straight
     * and get pixel integer color with x,y coordinates. <p>
     * 2-Step: Check whether pixel represent bound of player.<p>
     * 3-Step: Check whether exist Point which can be center coordinates of Player
     * and located below and to the right then given (x,y), if Yes - current {@code x} replace {@code  endPlayerBound}
     * and continue for cycle, if No - starting 4 Step.<p>
     * 4-Step: Find middle of bound Player and trying to add in Set new Player via {@code setPlayerCoordinate} method.<p>
     * 5-Step: If already exist point which locate in radius = 8 from given (x, y) point then set of players not modify,
     * else add new coordinate in corresponding Set in depend on player belong to.<p>
     */
    public GameInfo analyse() {
        setShadingField();
        baseAnalyseRun();
        searchOverlayPlayers();
        setPlayerPossessionOfBall();
        setPlaymateSide();
        setCornerState();
        searchNearlyPlayerIfActivePlayerNotFound();
        return new GameInfo(activePlayer, playmates, opposites, ball, isPlaymateBallPossession,
                isNobodyBallPossession, isShadingField, isCorner, playmateSide, pixels);
    }

    private void searchNearlyPlayerIfActivePlayerNotFound() {
        if (ball != null && playmates != null) {
            activePlayer = playmates.stream().min(Comparator.comparing(player -> player.distance(ball))).orElse(null);
        }
    }

    private void setCornerState() {
        if (ball != null) {
            isCorner = LEFT_TOP_CORNER.getPoint().distance(ball) < CORNER_POINT_DISTANCE
                    || LEFT_BOTTOM_CORNER.getPoint().distance(ball) < CORNER_POINT_DISTANCE
                    || RIGHT_TOP_CORNER.getPoint().distance(ball) < CORNER_POINT_DISTANCE
                    || RIGHT_BOTTOM_CORNER.getPoint().distance(ball) < CORNER_POINT_DISTANCE;
        }
    }

    @SuppressWarnings({"java:S127"})
    private void baseAnalyseRun() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int pixel = ImageUtils.getRGB(bufferedImage, x, y);
                pixels[x][y] = pixel;
                int xPrev = x;
                if (isBoundPlayerColor(pixel)) {
                    if (isExistBottomRightNearPoint(x, y)) {
                        x = getEndPlayerBound(x + 1, y, this::isBoundPlayerColor);
                        addSkippedValuesInPixels(xPrev, x, y);
                    } else {
                        x = addPlayer(x, y, this::isBoundPlayerColor, false);
                        addSkippedValuesInPixels(xPrev, x, y);
                    }
                } else if (activePlayer == null && isActivePlayerColor(pixel)) {
                    x = addPlayer(x, y, this::isActivePlayerColor, true);
                    addSkippedValuesInPixels(xPrev, x, y);
                } else if (ball == null && isBallColor(pixel)) {
                    ball = new Point(x + 1, y + 5);
                }
            }
        }
    }

    private void addSkippedValuesInPixels(int xPrev, int x, int y) {
        if (xPrev < x) {
            for (int i = xPrev + 1; i <= x; i++) {
                pixels[i][y] = ImageUtils.getRGB(bufferedImage, i, y);
            }
        }
    }

    private void setShadingField() {
        int shades = 0;
        shades = isShadingFieldColor(bufferedImage.getRGB(10, 10)) ? shades + 1 : shades;
        shades = isShadingFieldColor(bufferedImage.getRGB(WIDTH - 10, 10)) ? shades + 1 : shades;
        shades = isShadingFieldColor(bufferedImage.getRGB(10, HEIGHT - 10)) ? shades + 1 : shades;
        shades = isShadingFieldColor(bufferedImage.getRGB(WIDTH - 10, HEIGHT - 10)) ? shades + 1 : shades;
        isShadingField = shades > 1;
    }

    private void setPlaymateSide() {
        Stream<Point> players = Stream.concat(playmates.stream(), opposites.stream());
        Point mostLeftPlayer = players.min(Comparator.comparing(Point::getX)).orElse(null);
        if (playmates.contains(mostLeftPlayer)) {
            playmateSide = GameConstantsEnum.LEFT_PLAYMATE_SIDE;
        } else if (opposites.contains(mostLeftPlayer)) {
            playmateSide = GameConstantsEnum.RIGHT_PLAYMATE_SIDE;
        }
    }

    private void setPlayerPossessionOfBall() {
        if (ball == null) {
            isNobodyBallPossession = true;
            return;
        }
        Comparator<Point> closestToBall = Comparator.comparing(point -> point.distance(ball));
        Point playmate = playmates.stream().min(closestToBall).orElse(new Point(Integer.MAX_VALUE, Integer.MAX_VALUE));
        Point opposite = opposites.stream().min(closestToBall).orElse(new Point(Integer.MAX_VALUE, Integer.MAX_VALUE));
        double playmateDistance = playmate.distance(ball);
        double oppositeDistance = opposite.distance(ball);
        if (Math.abs(playmateDistance - oppositeDistance) <= 1
                || (playmateDistance > BALL_POSSESSION_RADIUS && oppositeDistance > BALL_POSSESSION_RADIUS)) {
            isNobodyBallPossession = true;
        } else if (playmateDistance < oppositeDistance) {
            isPlaymateBallPossession = true;
        }
    }

    private void searchOverlayPlayers() {
        boolean playersNotEnough = opposites.size() < 11 || playmates.size() < 11;

        if (ball != null && playersNotEnough) {
            // looking in the vicinity of the ball
            searchOverlayPlayersBase(
                    new SearchConditions(ball, true, false, false),
                    this::addOverlayPlayersBase
            );
        }
        if (playersNotEnough) {
            // if playmate on opposite or vice versa
            searchOtherPlayerOverlay();
        }
        // if ball completely hiding of opposite
        if (ball != null) {
            searchOverlayPlayersBase(
                    new SearchConditions(ball, true, false, false),
                    this::addFullOverlayBallPlayer
            );
        }
    }

    private void searchOtherPlayerOverlay() {
        if (playmates.size() < 11) {
            opposites.forEach(opponent -> searchOverlayPlayersBase(
                    new SearchConditions(opponent, false, true, false),
                    this::addOverlayPlayersBase
            ));
        }
        if (opposites.size() < 11) {
            playmates.forEach(playmate -> searchOverlayPlayersBase(
                    new SearchConditions(playmate, false, false, true),
                    this::addOverlayPlayersBase
            ));
        }
    }

    private void searchOverlayPlayersBase(SearchConditions search, Consumer<SearchConditions> addOverlayPlayer) {
        Point leftTopScanPoint = new Point(
                search.point.x - OVERLAY_PLAYER_BASE_DISTANCE, search.point.y - OVERLAY_PLAYER_BASE_DISTANCE);
        Point bottomRightScanPoint = new Point(
                search.point.x + OVERLAY_PLAYER_BASE_DISTANCE, search.point.y + OVERLAY_PLAYER_BASE_DISTANCE);

        if (leftTopScanPoint.x >= 0 && leftTopScanPoint.y >= 0
                && bottomRightScanPoint.x < WIDTH && bottomRightScanPoint.y < HEIGHT) {

            for (int y = leftTopScanPoint.y; y <= bottomRightScanPoint.y; y++) {
                for (int x = leftTopScanPoint.x; x <= bottomRightScanPoint.x; x++) {
                    int pixel = pixels[x][y];
                    search.baseData = new SearchConditions.BaseData(x, y, pixel);
                    addOverlayPlayer.accept(search);
                }
            }
        }
    }

    private void addOverlayPlayersBase(SearchConditions search) {
        if ((search.isOpposite || search.isBall) && opposites.size() < 11
                && (isOverlayOppositePlayerColor(search.baseData.pixel)
                || isOppositeColor(search.baseData.pixel))) {
            boolean isExistPoint = opposites.stream()
                    .anyMatch(p -> p.distance(search.baseData.x, search.baseData.y) < OVERLAY_PLAYER_BASE_DISTANCE);
            if (!isExistPoint) {
                opposites.add(new Point(search.baseData.x, search.baseData.y));
            }
        }
        if ((search.isPlaymate || search.isBall) && playmates.size() < 11
                && (isOverlayPlaymatePlayerColor(search.baseData.pixel)
                || isPlaymateColor(search.baseData.pixel))) {
            boolean isExistPoint = playmates.stream()
                    .anyMatch(p -> p.distance(search.baseData.x, search.baseData.y) < OVERLAY_PLAYER_BASE_DISTANCE);
            if (!isExistPoint) {
                playmates.add(new Point(search.baseData.x, search.baseData.y));
            }
        }
    }

    private void addFullOverlayBallPlayer(SearchConditions search) {
        if (opposites.size() < 11 && search.isBall
                && (isBoundPlayerColor(search.baseData.pixel) || isOverlayBoundPlayerColor(search.baseData.pixel))) {
            boolean isExistPoint = opposites.stream()
                    .anyMatch(p -> p.distance(search.baseData.x, search.baseData.y) < OVERLAY_PLAYER_BASE_DISTANCE);
            if (!isExistPoint) {
                opposites.add(new Point(search.baseData.x, search.baseData.y));
            }
        }
    }

    private int addPlayer(int x, int y, Function<Integer, Boolean> isBoundColor, boolean isActivePlayer) {
        int endPlayerBound = getEndPlayerBound(x + 1, y, isBoundColor);
        // if players stick together
        if (endPlayerBound - x > 7) {
            endPlayerBound = endPlayerBound - (endPlayerBound - x) / 2;
        }
        int middlePlayerBound = endPlayerBound - (endPlayerBound - x) / 2;
        // fix double-crossing bound
        if (checkDoubleCrossingBound(middlePlayerBound, y + 4, y - 4, isActivePlayer)) {
            return x;
        }
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

    private boolean checkDoubleCrossingBound(int x, int topRange, int bottomRange, boolean isActivePlayer) {
        if (isShadingField) {
            return false;
        }
        if (bottomRange > 0 && topRange < HEIGHT) {
            for (int y = bottomRange; y <= topRange; y++) {
                int pixel = ImageUtils.getRGB(bufferedImage, x, y);
                if ((!isActivePlayer && isActivePlayerColor(pixel)) || (isActivePlayer && isBoundPlayerColor(pixel))) {
                    return true;
                }
            }
        }
        return false;
    }

    // if player already was added
    private boolean isExistBottomRightNearPoint(int x, int y) {
        if (y + 4 < HEIGHT) {
            int pixel = ImageUtils.getRGB(bufferedImage, x, y + 4);
            boolean isPlaymate = isPlaymateColor(pixel);
            Stream<Point> players = isPlaymate ? playmates.stream() : opposites.stream();
            return players.anyMatch(
                    p -> (p.x >= x && p.x - x < X_DISTANCE_NEARLY_PLAYER) && (p.y >= y && p.y - y < Y_DISTANCE_NEARLY_PLAYER));
        }
        return false;
    }

    private int getEndPlayerBound(int x, int y, Function<Integer, Boolean> isBoundColor) {
        if (x < WIDTH) {
            int pixel = ImageUtils.getRGB(bufferedImage, x, y);
            if (Boolean.TRUE.equals(isBoundColor.apply(pixel))) {
                return getEndPlayerBound(x + 1, y, isBoundColor);
            }
            return x - 1;
        }
        return x - 1;
    }

    private boolean setPlayerCoordinate(int x, int y, boolean isActivePlayer) {
        if (y > 0 && y < HEIGHT) {
            int pixel = ImageUtils.getRGB(bufferedImage, x, y);
            Predicate<SortedSet<Point>> isExistPoint = players -> players.stream()
                    .anyMatch(point -> point.distance(x, y) < SEARCH_RADIUS_NEARLY_PLAYER);

            if (isActivePlayer) {
                activePlayer = new Point(x, y);
                return playmates.add(activePlayer);
            }

            int test1 = ImageUtils.getRGB(bufferedImage, Math.min(x + 1, WIDTH - 1), Math.min(y + 1, HEIGHT - 1));
            int test2 = ImageUtils.getRGB(bufferedImage, Math.min(x + 2, WIDTH - 1), Math.min(y + 2, HEIGHT - 1));
            int test3 = ImageUtils.getRGB(bufferedImage, Math.min(x + 3, WIDTH - 1), Math.min(y + 3, HEIGHT - 1));

            if (isPlaymateColor(pixel)) {
                return (isPlaymateColor(test1) || isPlaymateColor(test2) || isPlaymateColor(test3))
                        && !isExistPoint.test(playmates) && playmates.add(new Point(x, y));
            } else if (isOppositeColor(pixel)) {
                return (isOppositeColor(test1) || isOppositeColor(test2) || isOppositeColor(test3))
                        && !isExistPoint.test(opposites) && opposites.add(new Point(x, y));
            }
            return false;
        }
        return false;
    }

    private boolean isBoundPlayerColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        if (isShadingField) {
            return SHADING_BOUND_OF_PLAYER_COLOR_LOWER.getColor().getRed() <= r && BOUND_OF_PLAYER_COLOR_UPPER.getColor().getRed() >= r
                    && SHADING_BOUND_OF_PLAYER_COLOR_LOWER.getColor().getGreen() <= g && BOUND_OF_PLAYER_COLOR_UPPER.getColor().getGreen() >= g
                    && SHADING_BOUND_OF_PLAYER_COLOR_LOWER.getColor().getBlue() <= b && BOUND_OF_PLAYER_COLOR_UPPER.getColor().getBlue() >= b;
        }
        return BOUND_OF_PLAYER_COLOR_LOWER.getColor().getRed() < r && BOUND_OF_PLAYER_COLOR_UPPER.getColor().getRed() > r
                && BOUND_OF_PLAYER_COLOR_LOWER.getColor().getGreen() < g && BOUND_OF_PLAYER_COLOR_UPPER.getColor().getGreen() > g
                && BOUND_OF_PLAYER_COLOR_LOWER.getColor().getBlue() < b && BOUND_OF_PLAYER_COLOR_UPPER.getColor().getBlue() > b;
    }

    private boolean isPlaymateColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        if (isShadingField) {
            return SHADING_PLAYMATE_COLOR_LOWER.getColor().getRed() <= r && SHADING_PLAYMATE_COLOR_UPPER.getColor().getRed() >= r
                    && SHADING_PLAYMATE_COLOR_LOWER.getColor().getGreen() <= g && SHADING_PLAYMATE_COLOR_UPPER.getColor().getGreen() >= g
                    && SHADING_PLAYMATE_COLOR_LOWER.getColor().getBlue() <= b && SHADING_PLAYMATE_COLOR_UPPER.getColor().getBlue() >= b;
        }
        return PLAYMATE_COLOR_LOWER.getColor().getRed() < r && PLAYMATE_COLOR_UPPER.getColor().getRed() > r
                && PLAYMATE_COLOR_LOWER.getColor().getGreen() > g && PLAYMATE_COLOR_UPPER.getColor().getGreen() < g
                && PLAYMATE_COLOR_LOWER.getColor().getBlue() > b && PLAYMATE_COLOR_UPPER.getColor().getBlue() < b;
    }

    private boolean isOppositeColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        if (isShadingField) {
            return SHADING_OPPOSITE_COLOR_LOWER.getColor().getRed() <= r && SHADING_OPPOSITE_COLOR_UPPER.getColor().getRed() >= r
                    && SHADING_OPPOSITE_COLOR_LOWER.getColor().getGreen() <= g && SHADING_OPPOSITE_COLOR_UPPER.getColor().getGreen() >= g
                    && SHADING_OPPOSITE_COLOR_LOWER.getColor().getBlue() <= b && SHADING_OPPOSITE_COLOR_UPPER.getColor().getBlue() >= b;
        }
        return OPPOSITE_COLOR_LOWER.getColor().getRed() > r && OPPOSITE_COLOR_UPPER.getColor().getRed() < r
                && OPPOSITE_COLOR_LOWER.getColor().getGreen() > g && OPPOSITE_COLOR_UPPER.getColor().getGreen() < g
                && OPPOSITE_COLOR_LOWER.getColor().getBlue() < b && OPPOSITE_COLOR_UPPER.getColor().getBlue() > b;
    }

    private boolean isActivePlayerColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        if (isShadingField) {
            return SHADING_ACTIVE_PLAYER_LOWER.getColor().getRed() <= r && SHADING_ACTIVE_PLAYER_UPPER.getColor().getRed() >= r
                    && SHADING_ACTIVE_PLAYER_LOWER.getColor().getGreen() <= g && SHADING_ACTIVE_PLAYER_UPPER.getColor().getGreen() >= g
                    && ((SHADING_ACTIVE_PLAYER_LOWER.getColor().getBlue() <= b && SHADING_ACTIVE_PLAYER_UPPER.getColor().getBlue() >= b)
                    || isShadingField);
        }
        return ACTIVE_PLAYER_LOWER.getColor().getRed() > r && ACTIVE_PLAYER_UPPER.getColor().getRed() <= r
                && ACTIVE_PLAYER_LOWER.getColor().getGreen() < g && ACTIVE_PLAYER_UPPER.getColor().getGreen() > g
                && ACTIVE_PLAYER_LOWER.getColor().getBlue() < b && ACTIVE_PLAYER_UPPER.getColor().getBlue() > b
                && Math.abs(g - b) < 30 && Math.abs(r - g) > 100;
    }

    private boolean isBallColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        if (isShadingField) {
            return SHADING_BALL_COLOR_LOWER.getColor().getRed() <= r && SHADING_BALL_COLOR_UPPER.getColor().getRed() >= r
                    && SHADING_BALL_COLOR_LOWER.getColor().getGreen() <= g && SHADING_BALL_COLOR_UPPER.getColor().getGreen() >= g
                    && SHADING_BALL_COLOR_LOWER.getColor().getBlue() <= b && SHADING_BALL_COLOR_UPPER.getColor().getBlue() >= b;
        }
        return BALL_COLOR_LOWER.getColor().getRed() < r && BALL_COLOR_UPPER.getColor().getRed() > r
                && BALL_COLOR_LOWER.getColor().getGreen() < g && BALL_COLOR_UPPER.getColor().getGreen() > g
                && BALL_COLOR_LOWER.getColor().getBlue() >= b && BALL_COLOR_UPPER.getColor().getBlue() <= b;
    }

    private boolean isOverlayOppositePlayerColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        return OVERLAY_OPPOSITE_PLAYER_COLOR_LOWER.getColor().getRed() <= r && OVERLAY_OPPOSITE_PLAYER_COLOR_UPPER.getColor().getRed() >= r
                && OVERLAY_OPPOSITE_PLAYER_COLOR_LOWER.getColor().getGreen() <= g && OVERLAY_OPPOSITE_PLAYER_COLOR_UPPER.getColor().getGreen() >= g
                && OVERLAY_OPPOSITE_PLAYER_COLOR_LOWER.getColor().getBlue() <= b && OVERLAY_OPPOSITE_PLAYER_COLOR_UPPER.getColor().getBlue() >= b;
    }

    private boolean isOverlayPlaymatePlayerColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        return OVERLAY_PLAYMATE_PLAYER_COLOR_LOWER.getColor().getRed() <= r && OVERLAY_PLAYMATE_PLAYER_COLOR_UPPER.getColor().getRed() >= r
                && OVERLAY_PLAYMATE_PLAYER_COLOR_LOWER.getColor().getGreen() <= g && OVERLAY_PLAYMATE_PLAYER_COLOR_UPPER.getColor().getGreen() >= g
                && OVERLAY_PLAYMATE_PLAYER_COLOR_LOWER.getColor().getBlue() >= b && OVERLAY_PLAYMATE_PLAYER_COLOR_UPPER.getColor().getBlue() <= b;
    }

    private boolean isOverlayBoundPlayerColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        return OVERLAY_BOUND_OF_PLAYER_COLOR.getColor().getRed() <= r && BOUND_OF_PLAYER_COLOR_LOWER.getColor().getRed() >= r
                && OVERLAY_BOUND_OF_PLAYER_COLOR.getColor().getGreen() <= g && BOUND_OF_PLAYER_COLOR_LOWER.getColor().getGreen() >= g
                && OVERLAY_BOUND_OF_PLAYER_COLOR.getColor().getBlue() <= b && BOUND_OF_PLAYER_COLOR_LOWER.getColor().getBlue() >= b
                && Math.abs(g - b) < 10 && Math.abs(r - g) < 10 && Math.abs(r - b) < 10;
    }

    private boolean isShadingFieldColor(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;

        return SHADING_FIELD_COLOR_LOWER.getColor().getRed() <= r && SHADING_FIELD_COLOR_UPPER.getColor().getRed() >= r
                && SHADING_FIELD_COLOR_LOWER.getColor().getGreen() <= g && SHADING_FIELD_COLOR_UPPER.getColor().getGreen() >= g
                && SHADING_FIELD_COLOR_LOWER.getColor().getBlue() <= b && SHADING_FIELD_COLOR_UPPER.getColor().getBlue() >= b
                && r < g && r > b && g - r <= 45 && g - b <= 90 && r - b <= 50;
    }
}
