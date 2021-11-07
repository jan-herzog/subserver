package de.nebelniek.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Prices {

    GUILD_CREATE(10000),
    GUILD_CLAIM_REGION(5000),
    GUILD_EXPAND_REGION(5000),
    GUILD_CHANGE_COLOR(5000),
    GUILD_RENAME(10000),
    GUILD_CHANGE_PREFIX(15000),
    ;

    @Getter
    private int price;
}
