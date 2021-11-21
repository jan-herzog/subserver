package de.nebelniek.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TwitchTokens {
    HELIXTOKEN("sipgw170fq90b2rqqlt324tutow8bm",""),
    NEBELNIEK("0xt8va06nf4up9rw561aznczvp4oeg","492287858")
    ;

    private String token;
    private String channelId;

    @Override
    public String toString() {
        return this.token;
    }
}
