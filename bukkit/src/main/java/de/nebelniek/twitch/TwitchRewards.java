package de.nebelniek.twitch;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TwitchRewards {

    POINTS_5000("", 5000),
    POINTS_10000("", 5000),
    POINTS_30000("", 5000),
    POINTS_100000("", 5000);

    private final String name;
    private int price;


}
