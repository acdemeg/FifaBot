package org.example;

import java.awt.*;
import java.util.SortedSet;

@SuppressWarnings("ClassCanBeRecord")
public class GameInfo {

    public static final int startX = 831;
    public static final int startY = 882;
    public static final int width = 258;
    public static final int height = 153;
    public static final Color playmateColorLower = new Color(126,35,24);
    public static final Color playmateColorUpper = new Color(220,50,55);
    public static final Color oppositeColorLower = new Color(35,40,70);
    public static final Color oppositeColorUpper = new Color(45,50,83);
    public static final Color activePlayerLower = new Color(25,100,100);
    public static final Color activePlayerUpper = new Color(0,210,210);
    public static final Color boundOfPlayerColor = new Color(235,235,235);

    private final SortedSet<Point> playmates;
    private final SortedSet<Point> opposites;
    private final Point activePlayer;

    public SortedSet<Point> getPlaymates() {
        return playmates;
    }

    public SortedSet<Point> getOpposites() {
        return opposites;
    }

    public GameInfo(SortedSet<Point> playmates, SortedSet<Point> opposites, Point activePlayer) {
        this.playmates = playmates;
        this.opposites = opposites;
        this.activePlayer = activePlayer;
    }
}
