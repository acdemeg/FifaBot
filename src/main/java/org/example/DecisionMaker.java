package org.example;

import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.example.GeometryUtils.calculateTriangleHeight;
import static org.example.GeometryUtils.getRectangleBetweenPlayers;

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
            searchAvailablePlaymatesForLowShot();

            return new ActionProducer(new GameAction(ControlsEnum.NONE, 0));
        }
        return new ActionProducer(new GameAction(ControlsEnum.NONE, 0));
    }

    private void searchAvailablePlaymatesForLowShot() {
        final Comparator<Point> comparator;
        if (gameInfo.getPlaymateSide().equals(GameConstantsEnum.LEFT_PLAYMATE_SIDE)) {
            comparator = Comparator.comparingDouble(Point::getX).reversed();
        }
        else {
            comparator = Comparator.comparingDouble(Point::getX);
        }
        SortedSet<Point> lowShotCandidates = new TreeSet<>(comparator);

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
                lowShotCandidates.add(playmate);
            }

        });

    }

    private boolean existThreatInterceptionOfBall(double lowShotDistance, Point activePlayer,
                                                  Point playmate, Point opposite) {
        // find height of triangle(distance to opposite from low shot vector)
        double height = calculateTriangleHeight(activePlayer, playmate, opposite);
        return (height / lowShotDistance) < 0.2;
    }
}
