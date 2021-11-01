package de.nebelniek.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TwitchTokens {
    HELIXTOKEN("",""),
    NEBELNIEK("","")
    ;

    private String token;
    private String channelId;

    @Override
    public String toString() {
        return this.token;
    }
}
