package de.nebelniek.inventory.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum ItemColors {

    GUILD(ChatColor.DARK_GREEN, ChatColor.GREEN),
    BANK(ChatColor.GOLD, ChatColor.YELLOW),
    REGION(ChatColor.BLUE, ChatColor.AQUA),
    MEMBER(ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE),
    LEAVE(ChatColor.DARK_RED, ChatColor.RED),
    HOME(ChatColor.DARK_GREEN, ChatColor.GREEN),

    ;

    private ChatColor primary;
    private ChatColor accent;

}
