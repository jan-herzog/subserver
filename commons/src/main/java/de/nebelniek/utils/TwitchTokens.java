package de.nebelniek.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TwitchTokens {
    HELIXTOKEN("sipgw170fq90b2rqqlt324tutow8bm",""),
    NEBELNIEK("7eiqwa76km0ymvckny929yq9eas24k","53292169")
    ;

    private String token;
    private String channelId;

    @Override
    public String toString() {
        return this.token;
    }
}
