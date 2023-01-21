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

    private static final double OPPOSITE_DISTANCE_LOW_SHOT_DISTANCE_RATIO = 0.25;
    private static final double DISTANCE_DELAY_MULTIPLICATION_FACTOR = 10;

    @NonNull
    private GameInfo gameInfo;

    public ActionProducer getActionProducer() {
        log.info(gameInfo.toString());
        shadingFieldHandle();
        Set<GameAction> gameActions = new HashSet<>();
        gameActionsFilling(gameActions);
        GameAction gameAction = pickActionWithBestPriority(gameActions);
        log.info(gameAction.toString());
        setGameHistory(gameAction.getActionTargetPlayer());

        return new ActionProducer(gameAction);
    }

    private void gameActionsFilling(Set<GameAction> gameActions) {
        gameActions.add(new GameAction(List.of(NONE), gameInfo.getActivePlayer()));
        gameActions.add(protectBallOrDefenceAction());
        if (gameInfo.isPlaymateBallPossession() && gameInfo.getActivePlayer() != null) {
            gameActions.add(attackShootAction());
            gameActions.add(searchAvailablePlaymatesForLowShot());
        }
    }

    private GameAction pickActionWithBestPriority(Set<GameAction> gameActions) {
        return gameActions.stream().min(
                        Comparator.comparing(action -> action.getControls().stream().min(
                                Comparator.comparing(ControlsEnum::getPriority)).orElse(NONE)))
                .orElse(new GameAction(List.of(NONE), gameInfo.getActivePlayer()));
    }

    private GameAction attackShootAction() {
        if (canAttackShoot()) {
            return new GameAction(List.of(ATTACK_SHOOT_VOLLEY_HEADER), gameInfo.getActivePlayer());
        }
        return new GameAction(List.of(NONE), gameInfo.getActivePlayer());
    }

    private GameAction protectBallOrDefenceAction() {
        if (gameInfo.isNobodyBallPossession()) {
            return new GameAction(List.of(ATTACK_PROTECT_BALL), gameInfo.getActivePlayer());
        }
        return new GameAction(List.of(NONE), gameInfo.getActivePlayer());
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

    // find available playmates for low pass
    private GameAction searchAvailablePlaymatesForLowShot() {
        final Comparator<Point> comparator;
        if (gameInfo.getPlaymateSide().equals(LEFT_PLAYMATE_SIDE)) {
            comparator = Comparator.comparingDouble(Point::getX).reversed().thenComparing(Point::getY);
        } else {
            comparator = Comparator.comparingDouble(Point::getX).thenComparing(Point::getY);
        }
        SortedMap<Point, Rectangle> lowShotCandidateAreaMap = new TreeMap<>(comparator);
        SortedMap<Point, Double> lowShotCandidateDistanceMap = new TreeMap<>(comparator);
        lowShotMapsFilling(lowShotCandidateAreaMap, lowShotCandidateDistanceMap);

        if (lowShotCandidateAreaMap.isEmpty()) {
            return new GameAction(List.of(ATTACK_PROTECT_BALL), gameInfo.getActivePlayer());
        }

        return getGameActionForLowShotByDirection(
                lowShotCandidateAreaMap, lowShotCandidateDistanceMap);
    }

    private void lowShotMapsFilling(SortedMap<Point, Rectangle> lowShotCandidateAreaMap,
                                    SortedMap<Point, Double> lowShotCandidateDistanceMap) {
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
    }

    private GameAction getGameActionForLowShotByDirection(SortedMap<Point, Rectangle> lowShotCandidateAreaMap,
                                                          SortedMap<Point, Double> lowShotCandidateDistanceMap) {
        Point actionTargetPlayer = lowShotCandidateAreaMap.firstKey();
        Rectangle rectangleBetweenPlayers = lowShotCandidateAreaMap.get(actionTargetPlayer);
        double lowShotDistance = lowShotCandidateDistanceMap.get(actionTargetPlayer);

        GeomEnum direction = defineShotDirection(
                actionTargetPlayer, gameInfo.getActivePlayer(), gameInfo.getPlaymateSide(),
                rectangleBetweenPlayers.getWidth(), lowShotDistance);
        int delay = getDelayByDistanceValue(lowShotDistance);
        ATTACK_SHORT_PASS_HEADER.getDelay().set(delay);
        ArrayList<ControlsEnum> controls = new ArrayList<>(direction.getControlsList());
        controls.add(ATTACK_SHORT_PASS_HEADER);

        return new GameAction(controls, actionTargetPlayer);
    }

    private int getDelayByDistanceValue(double distance) {
        return (int) (distance * DISTANCE_DELAY_MULTIPLICATION_FACTOR);
    }

    private boolean existThreatInterceptionOfBall(double lowShotDistance, Point activePlayer,
                                                  Point playmate, Point opposite) {
        // find height of triangle(distance to opposite from low shot vector)
        double height = calculateTriangleHeight(activePlayer, playmate, opposite);
        return (height / lowShotDistance) < OPPOSITE_DISTANCE_LOW_SHOT_DISTANCE_RATIO;
    }

    private void setGameHistory(Point actionTargetPlayer) {
        GameHistory.setPrevGameInfo(gameInfo);
        GameHistory.setPrevActionTarget(actionTargetPlayer);
        log.info("#setGameHistory -> Set values complete");
    }
}
