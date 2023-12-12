package org.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bot.enums.GameConstantsEnum;

import java.awt.*;
import java.util.Collections;
import java.util.SortedSet;

/**
 * In the class storing both static information which are time immutable
 * and dynamic data which are actually for one image screenshot
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class GameInfo {
    // for full window mode x = 836 y = 839
    public static final int START_X = 831;
    public static final int START_Y = 869;
    public static final int WIDTH = 258;
    public static final int HEIGHT = 153;

    private Point activePlayer;
    private final Point ball;
    private boolean isPlaymateBallPossession;
    private boolean isNobodyBallPossession;
    private final boolean isShadingField;
    private final boolean isCorner;
    private final GameConstantsEnum playmateSide;
    private SortedSet<Point> playmates = Collections.emptySortedSet();
    private SortedSet<Point> opposites = Collections.emptySortedSet();
    @ToString.Exclude
    private final int[][] pixels;

}
