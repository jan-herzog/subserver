package de.nebelniek.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Prefix {

    LOBBY("§eLobby §8» §7"),
    TWITCH("§5Twitch §8» §7"),
    GUILD("§2Gilden §8» §7"),
    GUILDCHAT("§2Gilden-Chat §8» §7"),
    COINS("§eCoins §8» §7"),
    ;

    private String prefix;

    @Override
    public String toString() {
        return this.prefix;
    }
}
