package de.nebelniek.twitch;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TwitchRewards {

    POINTS_5000("5k", 5000),
    POINTS_10000("10k", 10000),
    POINTS_30000("30k", 30000),
    POINTS_100000("100k", 100000);

    private final String name;
    private int price;


}
