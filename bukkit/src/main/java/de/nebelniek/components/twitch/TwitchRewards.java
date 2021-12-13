package de.nebelniek.components.twitch;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TwitchRewards {

    POINTS_5000("5K SUBSERVER PUNKTE", 5000),
    POINTS_10000("10K SUBSERVER PUNKTE", 10000),
    POINTS_30000("30K SUBSERVER PUNKTE", 30000),
    POINTS_100000("100K SUBSERVER PUNKTE", 100000);

    private final String name;
    private int price;


}
