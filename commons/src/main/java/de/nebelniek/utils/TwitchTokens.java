package de.nebelniek.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TwitchTokens {
    HELIXTOKEN("jc4gms5xf1bqpxqen366fftr0n0if1",""),
    NEBELNIEK("norgxs79btnhlyrwav4kc48jl0imj5","final ")
    ;

    private String token;
    private String channelId;

    @Override
    public String toString() {
        return this.token;
    }
}
