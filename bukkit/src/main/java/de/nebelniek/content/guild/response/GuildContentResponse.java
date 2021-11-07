package de.nebelniek.content.guild.response;

import lombok.Getter;

@Getter
public record GuildContentResponse(GuildResponseState state, String message) {
}
