package de.nebelniek.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Prefix {

    LOBBY("§eLobby §8» §7"),
    TWITCH("§5Twitch §8» §7"),
    DISCORD("§9Discord §8» §7"),
    LINK("§3Link §8» §7"),
    GUILD("§2Gilden §8» §7"),
    GUILDCHAT("§2Gilden-Chat §8» §7"),
    COINS("§6Coins §8» §7"),
    PROXY("§cProxy §8» §7"),
    COMBAT("§bCombat §8» §7"),
    SUBSERVER("§5Subserver §8» §7"),
    BAN("§4Ban §8» §7"),
    ;

    private String prefix;

    @Override
    public String toString() {
        return this.prefix;
    }
}
