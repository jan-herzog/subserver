package de.nebelniek.content.guild.response;

import lombok.Getter;

public record GuildContentResponse(GuildResponseState state, String message) {

    @Override
    public GuildResponseState state() {
        return state;
    }

    @Override
    public String message() {
        return message;
    }
}
