package org.example;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.*;

/**
 * This class represent prev game states and it targets decision
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameHistory {
    @Getter
    @Setter
    private static GameInfo prevGameInfo;
    @Getter
    @Setter
    private static Point prevActionTarget;
}