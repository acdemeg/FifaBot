package org.example;


import lombok.Data;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.example.enums.ControlsEnum;
import org.example.enums.GameConstantsEnum;
import org.example.enums.GeomEnum;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;

import static org.example.GeometryUtils.*;
import static org.example.enums.ControlsEnum.*;
import static org.example.enums.GameConstantsEnum.*;

/**
 * This class take responsible for deciding by creating best {@code GameAction} based on {@code GameInfo} data
 */
@Log
@Data
public class DecisionMaker {

    private static final double OPPOSITE_DISTANCE_LOW_SHOT_DISTANCE_RATIO = 0.25;
    private static final int DISTANCE_DELAY_MULTIPLICATION_FACTOR = 10;
    private static final int FREE_FIELD_PART_SCAN_DISTANCE = 30;
    private final Set<GameAction> gameActions = new HashSet<>();
    @NonNull
    private GameInfo gameInfo;

    public ActionProducer decide() {
        log.info(gameInfo.toString()); // order 1
        shadingFieldHandle();
        gameActionsFilling();
        GameAction gameAction = pickActionWithBestPriority();
        log.info(gameAction.toString()); // order 3
        setGameHistory(gameAction.getActionTargetPlayer());

        return new ActionProducer(gameAction);
    }

    private void gameActionsFilling() {
        gameActions.add(new GameAction(List.of(NONE), gameInfo.getActivePlayer()));
        gameActions.add(protectBallOrDefenceAction());
        if (gameInfo.isPlaymateBallPossession() && gameInfo.getActivePlayer() != null) {
            gameActions.add(attackShootAction());
            gameActions.add(getLowShotAction());
            gameActions.add(getMovingAction());
        }
    }

    private GameAction pickActionWithBestPriority() {
        log.info(gameActions.toString()); // order 2
        return gameActions.stream().min(
                        Comparator.comparing(action -> action.getControls().stream().min(
                                Comparator.comparing(ControlsEnum::getPriority)).orElse(NONE)))
                .orElse(new GameAction(List.of(NONE), gameInfo.getActivePlayer()));
    }

    // find free part of field(no opposites in front of the playmate) for moving
    private GameAction getMovingAction() {

        boolean isLeft = gameInfo.getPlaymateSide().equals(LEFT_PLAYMATE_SIDE);
        GeomEnum direction = isLeft ? GeomEnum.RIGHT : GeomEnum.LEFT;
        GeomEnum bottomDirection = isLeft ? GeomEnum.BOTTOM_RIGHT : GeomEnum.BOTTOM_LEFT;
        GeomEnum topDirection = isLeft ? GeomEnum.TOP_RIGHT : GeomEnum.TOP_LEFT;

        Collection<Double> angles = getAngles(isLeft);

        return toDefineDirectionAndGetAction(direction, bottomDirection, topDirection, angles);
    }

    private GameAction toDefineDirectionAndGetAction(GeomEnum direction, GeomEnum bottomDirection,
                                                     GeomEnum topDirection, Collection<Double> angles) {

        boolean canRightOrLeftMove = angles.stream().noneMatch(angle -> angle < Math.PI / 6 && angle > -Math.PI / 6);
        boolean canBottomRightOrLeftMove = angles.stream().noneMatch(angle -> angle < -Math.PI / 6 && angle > -Math.PI / 2);
        boolean canTopRightOrLeftMove = angles.stream().noneMatch(angle -> angle < Math.PI / 2 && angle > Math.PI / 6);

        if (canRightOrLeftMove) {
            return new GameAction(direction.getControlsList(), gameInfo.getActivePlayer());
        }
        if (gameInfo.getActivePlayer().y < CENTER_FIELD_POINT.getPoint().y) {
            if (canBottomRightOrLeftMove) {
                return new GameAction(bottomDirection.getControlsList(), gameInfo.getActivePlayer());
            }
            if (canTopRightOrLeftMove) {
                return new GameAction(topDirection.getControlsList(), gameInfo.getActivePlayer());
            }
        } else {
            if (canTopRightOrLeftMove) {
                return new GameAction(topDirection.getControlsList(), gameInfo.getActivePlayer());
            }
            if (canBottomRightOrLeftMove) {
                return new GameAction(bottomDirection.getControlsList(), gameInfo.getActivePlayer());
            }
        }
        return new GameAction(List.of(NONE), gameInfo.getActivePlayer());
    }

    private Collection<Double> getAngles(boolean isLeft) {
        IntBinaryOperator compare = (x1, x2) -> {
            if (isLeft)
                return x1 > x2 ? 0 : 1;
            return x1 < x2 ? 0 : 1;
        };
        // find all opposites in right(left) rectangle
        Set<Point> opposites = gameInfo.getOpposites().stream().filter(
                point -> compare.applyAsInt(point.x, gameInfo.getActivePlayer().x) == 0
                        && Math.abs(gameInfo.getActivePlayer().x - point.x) < FREE_FIELD_PART_SCAN_DISTANCE
                        && Math.abs(gameInfo.getActivePlayer().y - point.y) < FREE_FIELD_PART_SCAN_DISTANCE
        ).collect(Collectors.toSet());
        // find all opposites in right(left) crescent
        Map<Point, Double> mapOppositesDistanceValue = opposites.stream().collect(
                Collectors.toMap(Function.identity(), point -> point.distance(gameInfo.getActivePlayer()))
        ).entrySet().stream().filter(entry -> entry.getValue() < FREE_FIELD_PART_SCAN_DISTANCE).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        // find all angles between Ox axis and hypotenuse(angle between active player and opposite)
        return mapOppositesDistanceValue.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> Math.asin(
                                (double) entry.getKey().y - gameInfo.getActivePlayer().y) / entry.getValue()
                )).values();
    }

    private GameAction attackShootAction() {
        if (canAttackShoot()) {
            return new GameAction(List.of(ATTACK_SHOOT_VOLLEY_HEADER), gameInfo.getActivePlayer());
        }
        return new GameAction(List.of(NONE), gameInfo.getActivePlayer());
    }

    private GameAction protectBallOrDefenceAction() {
        if (gameInfo.isNobodyBallPossession() && gameInfo.getActivePlayer() != null) {
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
            log.fine("#shadingFieldHandle -> set values complete");
        }
    }

    // find available playmates for low shot pass
    private GameAction getLowShotAction() {
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
        log.fine("#setGameHistory -> Set values complete");
    }
}
