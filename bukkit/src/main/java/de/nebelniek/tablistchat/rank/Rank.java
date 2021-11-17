package de.nebelniek.tablistchat.rank;

import de.nebelniek.tablistchat.utils.NameUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
public enum Rank {

    ADMIN(10),
    TEAM(9),
    MOD(7),
    SUB(3),
    DEFAULT(1);

    @Getter
    private int power;

    public static Rank getRank(Player player) {
        String prefix = NameUtils.getPrefix(player.getUniqueId());
        if (prefix.contains("Sub"))
            return SUB;
        if (prefix.contains("Mod"))
            return MOD;
        if (prefix.contains("Team"))
            return TEAM;
        if (prefix.contains("Admin"))
            return ADMIN;
        return DEFAULT;
    }

    public boolean isHigherThanOrEquals(Rank rank) {
        return this.getPower() >= rank.getPower();
    }

}
