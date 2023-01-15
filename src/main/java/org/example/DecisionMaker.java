package org.example;

import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.example.GeometryUtils.*;

/**
 * This class take response of deciding by creating best {@code GameAction} based on {@code GameInfo} data
 */
@RequiredArgsConstructor
public class DecisionMaker {

    private final GameInfo gameInfo;

    public ActionProducer getActionProducer() {
        if (gameInfo.getPlaymates().isEmpty()) {
            return new ActionProducer(new GameAction(ControlsEnum.NONE, 0));
        }
        if (gameInfo.isNobodyBallPossession()) {
            return new ActionProducer(new GameAction(ControlsEnum.ATTACK_PROTECT_BALL, 0));
        }
        if (gameInfo.isPlaymateBallPossession()) {
            // find available playmates for low pass
            GameAction lowShotAction = searchAvailablePlaymatesForLowShot();

            return new ActionProducer(lowShotAction);
        }
        return new ActionProducer(new GameAction(ControlsEnum.NONE, 0));
    }

    private GameAction searchAvailablePlaymatesForLowShot() {
        final Comparator<Point> comparator;
        if (gameInfo.getPlaymateSide().equals(GameConstantsEnum.LEFT_PLAYMATE_SIDE)) {
            comparator = Comparator.comparingDouble(Point::getX).reversed();
        }
        else {
            comparator = Comparator.comparingDouble(Point::getX);
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

        getLowShotDirection(lowShotCandidateAreaMap, lowShotCandidateDistanceMap);
        return null;
    }

    private void getLowShotDirection(SortedMap<Point, Rectangle> lowShotCandidateAreaMap,
                                     SortedMap<Point, Double> lowShotCandidateDistanceMap) {
        Point shotCandidate = lowShotCandidateAreaMap.firstKey();
        Rectangle rectangleBetweenPlayers = lowShotCandidateAreaMap.get(shotCandidate);
        double lowShotDistance = lowShotCandidateDistanceMap.get(shotCandidate);

        GeomEnum direction = defineShotDirection(shotCandidate, gameInfo.getActivePlayer(), gameInfo.getPlaymateSide(),
                rectangleBetweenPlayers.getWidth(), lowShotDistance);
    }

    private boolean existThreatInterceptionOfBall(double lowShotDistance, Point activePlayer,
                                                  Point playmate, Point opposite) {
        // find height of triangle(distance to opposite from low shot vector)
        double height = calculateTriangleHeight(activePlayer, playmate, opposite);
        return (height / lowShotDistance) < 0.2;
    }
}
