package de.nebelniek.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Prefix {

    LOBBY("§eLobby §8» §7"),
    TWITCH("§5Twitch §8» §7"),
    ;

    private String prefix;

    @Override
    public String toString() {
        return this.prefix;
    }
}
