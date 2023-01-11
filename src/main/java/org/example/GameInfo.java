package org.example;

import lombok.Data;

import java.awt.*;
import java.util.SortedSet;

@Data
public class GameInfo {

    public static final int START_X = 831;
    public static final int START_Y = 869;
    public static final int WIDTH = 258;
    public static final int HEIGHT = 153;

    public static final Color playmateColorLower = new Color(100,20,20);
    public static final Color playmateColorUpper = new Color(220,50,55);
    public static final Color oppositeColorLower = new Color(35,40,70);
    public static final Color oppositeColorUpper = new Color(45,50,83);
    public static final Color activePlayerLower = new Color(25,100,100);
    public static final Color activePlayerUpper = new Color(0,210,210);
    public static final Color boundOfPlayerColor = new Color(222,222,222);
    public static final Color overlayBoundOfPlayerColor = new Color(130,130,130);
    public static final Color ballColorLower = new Color(180,135,1);
    public static final Color ballColorUpper = new Color(255,186,0);
    public static final Color overlayOppositePlayerColorLower = new Color(21,25,40);
    public static final Color overlayOppositePlayerColorUpper = new Color(36,45,60);
    public static final Color overlayPlaymatePlayerColorLower = new Color(100,45,16);
    public static final Color overlayPlaymatePlayerColorUpper = new Color(115,60,8);


    private final SortedSet<Point> playmates;
    private final SortedSet<Point> opposites;
    private final Point activePlayer;
    private final Point ball;
    private final boolean isPlaymateBallPossession;
    private final boolean isNobodyBallPossession;
    private final int[][] pixels;
}
