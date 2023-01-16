package org.example;

import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.ControlsEnum.ATTACK_SHORT_PASS_HEADER;
import static org.example.GeometryUtils.*;

/**
 * This class take response of deciding by creating best {@code GameAction} based on {@code GameInfo} data
 */
@RequiredArgsConstructor
public class DecisionMaker {

    public static final double OPPOSITE_DISTANCE_LOW_SHOT_DISTANCE_RATIO = 0.25;
    private final GameInfo gameInfo;

    public ActionProducer getActionProducer() {
        if (gameInfo.getPlaymates().isEmpty()) {
            return new ActionProducer(new GameAction(List.of(ControlsEnum.NONE), 0));
        }
        if (gameInfo.isNobodyBallPossession()) {
            return new ActionProducer(new GameAction(List.of(ControlsEnum.ATTACK_PROTECT_BALL), 0));
        }
        if (gameInfo.isPlaymateBallPossession()) {
            // find available playmates for low pass
            GameAction lowShotAction = searchAvailablePlaymatesForLowShot();

            return new ActionProducer(lowShotAction);
        }
        return new ActionProducer(new GameAction(List.of(ControlsEnum.NONE), 0));
    }

    private GameAction searchAvailablePlaymatesForLowShot() {
        final Comparator<Point> comparator;
        if (gameInfo.getPlaymateSide().equals(GameConstantsEnum.LEFT_PLAYMATE_SIDE)) {
            comparator = Comparator.comparingDouble(Point::getX).reversed().thenComparing(Point::getY);
        }
        else {
            comparator = Comparator.comparingDouble(Point::getX).thenComparing(Point::getY);
        }
        SortedMap<Point, Rectangle> lowShotCandidateAreaMap = new TreeMap<>(comparator);
        SortedMap<Point, Double> lowShotCandidateDistanceMap = new TreeMap<>(comparator);

        gameInfo.getPlaymates().forEach(playmate -> {

            Rectangle rectangleBetweenPlayers = getRectangleBetweenPlayers(playmate, gameInfo.getActivePlayer());
            final double lowShotDistance = gameInfo.getActivePlayer().distance(playmate);
            Set<Point> threateningOppositesIntoSquare = gameInfo.getOpposites().stream()
                    .filter(rectangleBetweenPlayers::contains)
                    .filter(opposite -> existThreatInterceptionOfBall(
                            lowShotDistance, gameInfo.getActivePlayer(), playmate, opposite)
                    )
                    .collect(Collectors.toSet());

            if (threateningOppositesIntoSquare.isEmpty()) {
                lowShotCandidateAreaMap.put(playmate, rectangleBetweenPlayers);
                lowShotCandidateDistanceMap.put(playmate, lowShotDistance);
            }

        });

        List<ControlsEnum> lowShotControls = getControlsForLowShotByDirection(
                lowShotCandidateAreaMap, lowShotCandidateDistanceMap);
        return new GameAction(lowShotControls, 0);
    }

    private List<ControlsEnum> getControlsForLowShotByDirection(SortedMap<Point, Rectangle> lowShotCandidateAreaMap,
                                                                SortedMap<Point, Double> lowShotCandidateDistanceMap) {
        Point shotCandidate = lowShotCandidateAreaMap.firstKey();
        Rectangle rectangleBetweenPlayers = lowShotCandidateAreaMap.get(shotCandidate);
        double lowShotDistance = lowShotCandidateDistanceMap.get(shotCandidate);

        GeomEnum direction = defineShotDirection(shotCandidate, gameInfo.getActivePlayer(), gameInfo.getPlaymateSide(),
                rectangleBetweenPlayers.getWidth(), lowShotDistance);
        int delay = getDelayByDistanceValue(lowShotDistance);
        ATTACK_SHORT_PASS_HEADER.getDelay().set(delay);
        direction.getControlsList().add(ATTACK_SHORT_PASS_HEADER);
        return direction.getControlsList();
    }

    private int getDelayByDistanceValue(double distance) {
        return (int) (distance * 10);
    }

    private boolean existThreatInterceptionOfBall(double lowShotDistance, Point activePlayer,
                                                  Point playmate, Point opposite) {
        // find height of triangle(distance to opposite from low shot vector)
        double height = calculateTriangleHeight(activePlayer, playmate, opposite);
        return (height / lowShotDistance) < OPPOSITE_DISTANCE_LOW_SHOT_DISTANCE_RATIO;
    }
}
