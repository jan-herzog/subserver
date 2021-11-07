package de.nebelniek.database.guild.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GuildRole {

    LEADER("§4Meister", "§4", 100),
    ADMIN("§cVize-Meister", "§c", 75),
    MOD("§aÄltester", "§a", 50),
    DEFAULT("§7Mitglied", "§7", 25);

    private final String prettyName;
    private final String color;
    private final int power;

    public boolean isHigherOrEquals(GuildRole other) {
        return this.getPower() >= other.getPower();
    }


    @Override
    public String toString() {
        return this.name();
    }
}
