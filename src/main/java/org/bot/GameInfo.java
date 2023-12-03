package org.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.bot.enums.GameConstantsEnum;

import java.awt.*;
import java.util.SortedSet;

/**
 * In the class storing both static information which are time immutable
 * and dynamic data which are actually for one image screenshot
 */
@Data
@ToString
@AllArgsConstructor
public class GameInfo {
    // for full window mode x = 836 y = 839
    public static final int START_X = 831;
    public static final int START_Y = 869;
    public static final int WIDTH = 258;
    public static final int HEIGHT = 153;

    private Point activePlayer;
    private final SortedSet<Point> playmates;
    private final SortedSet<Point> opposites;
    private final Point ball;
    private boolean isPlaymateBallPossession;
    private boolean isNobodyBallPossession;
    private final boolean isShadingField;
    private final GameConstantsEnum playmateSide;
    @ToString.Exclude
    private final int[][] pixels;

    public boolean isEmptyState() {
        return activePlayer == null
                && playmates.isEmpty()
                && opposites.isEmpty()
                && ball == null
                && playmateSide == null
                && !isShadingField;
    }
}
