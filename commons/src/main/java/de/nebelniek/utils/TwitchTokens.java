package de.nebelniek.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TwitchTokens {
    HELIXTOKEN("sipgw170fq90b2rqqlt324tutow8bm",""),
    NEBELNIEK("vu4ews7db43s6zilqnq133xmcbkexi","492287858")
    ;

    private String token;
    private String channelId;

    @Override
    public String toString() {
        return this.token;
    }
}
