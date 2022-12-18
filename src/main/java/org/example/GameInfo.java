package org.example;

import java.awt.*;
import java.util.SortedSet;

public class GameInfo {

    public static final int startX = 831;
    public static final int startY = 831;
    public static final int width = 258;
    public static final int height = 153;
    public static final int playmateColor = new Color(198,41,44).getRGB();
    public static final int oppositeColor = new Color(40,45,77).getRGB();
    public static final Color boundOfPlayerColor = new Color(235,235,235);

    private final SortedSet<Point> playmates;
    private final SortedSet<Point> opposites;

    public SortedSet<Point> getPlaymates() {
        return playmates;
    }

    public SortedSet<Point> getOpposites() {
        return opposites;
    }

    public GameInfo(SortedSet<Point> playmates, SortedSet<Point> opposites) {
        this.playmates = playmates;
        this.opposites = opposites;
    }
}
