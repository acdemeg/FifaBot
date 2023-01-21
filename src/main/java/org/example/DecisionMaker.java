package org.example;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.enums.ControlsEnum;
import org.example.enums.GameConstantsEnum;
import org.example.enums.GeomEnum;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.GeometryUtils.*;
import static org.example.enums.ControlsEnum.*;
import static org.example.enums.GameConstantsEnum.*;

/**
 * This class take responsible for deciding by creating best {@code GameAction} based on {@code GameInfo} data
 */
@Log
@RequiredArgsConstructor
public class DecisionMaker {

    public static final double OPPOSITE_DISTANCE_LOW_SHOT_DISTANCE_RATIO = 0.25;
    @NonNull
    private GameInfo gameInfo;
    private Point actionTargetPlayer;

    public ActionProducer getActionProducer() {
        log.info(gameInfo.toString());
        ActionProducer keyboard = new ActionProducer(new GameAction(List.of(NONE)));
        shadingFieldHandle();

        if (gameInfo.getPlaymates().isEmpty() || gameInfo.getActivePlayer() == null) {
            keyboard = new ActionProducer(new GameAction(List.of(NONE)));
        } else if (gameInfo.isNobodyBallPossession()) {
            actionTargetPlayer = gameInfo.getActivePlayer();
            keyboard = new ActionProducer(new GameAction(List.of(ATTACK_PROTECT_BALL)));
        } else if (gameInfo.isPlaymateBallPossession() && gameInfo.getActivePlayer() != null) {
            if (canAttackShoot()) {
                keyboard = new ActionProducer(new GameAction(List.of(ATTACK_SHOOT_VOLLEY_HEADER)));
            } else {
                // find available playmates for low pass
                GameAction lowShotAction = searchAvailablePlaymatesForLowShot();
                keyboard = new ActionProducer(lowShotAction);
            }
        }

        setGameHistory();
        log.info(keyboard.getGameAction().toString());

        return keyboard;
    }

    private boolean canAttackShoot() {
        GameConstantsEnum penaltyArea = gameInfo.getPlaymateSide().equals(LEFT_PLAYMATE_SIDE)
                ? RIGHT_PENALTY_AREA : LEFT_PENALTY_AREA;
        return penaltyArea.getRectangle().contains(gameInfo.getActivePlayer());
    }

    private void shadingFieldHandle() {
        if (gameInfo.isShadingField() && GameHistory.getPrevGameInfo() != null) {
            gameInfo = GameHistory.getPrevGameInfo();
            gameInfo.setActivePlayer(GameHistory.getPrevActionTarget());
            log.info("#shadingFieldHandle -> set values complete");
        }
    }

    private void setGameHistory() {
        if (actionTargetPlayer != null) {
            GameHistory.setPrevGameInfo(gameInfo);
            GameHistory.setPrevActionTarget(actionTargetPlayer);
            log.info("#setGameHistory -> Set values complete");
        }
    }

    private GameAction searchAvailablePlaymatesForLowShot() {
        final Comparator<Point> comparator;
        if (gameInfo.getPlaymateSide().equals(LEFT_PLAYMATE_SIDE)) {
            comparator = Comparator.comparingDouble(Point::getX).reversed().thenComparing(Point::getY);
        } else {
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

        if (lowShotCandidateAreaMap.isEmpty()) {
            // TODO if no candidates add logic
            actionTargetPlayer = gameInfo.getActivePlayer();
            return new GameAction(List.of(ATTACK_PROTECT_BALL));
        }

        List<ControlsEnum> lowShotControls = getControlsForLowShotByDirection(
                lowShotCandidateAreaMap, lowShotCandidateDistanceMap);

        return new GameAction(lowShotControls);
    }

    private List<ControlsEnum> getControlsForLowShotByDirection(SortedMap<Point, Rectangle> lowShotCandidateAreaMap,
                                                                SortedMap<Point, Double> lowShotCandidateDistanceMap) {
        actionTargetPlayer = lowShotCandidateAreaMap.firstKey();
        Rectangle rectangleBetweenPlayers = lowShotCandidateAreaMap.get(actionTargetPlayer);
        double lowShotDistance = lowShotCandidateDistanceMap.get(actionTargetPlayer);

        GeomEnum direction = defineShotDirection(
                actionTargetPlayer, gameInfo.getActivePlayer(), gameInfo.getPlaymateSide(),
                rectangleBetweenPlayers.getWidth(), lowShotDistance);
        int delay = getDelayByDistanceValue(lowShotDistance);
        ATTACK_SHORT_PASS_HEADER.getDelay().set(delay);
        ArrayList<ControlsEnum> controls = new ArrayList<>(direction.getControlsList());
        controls.add(ATTACK_SHORT_PASS_HEADER);
        return controls;
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
