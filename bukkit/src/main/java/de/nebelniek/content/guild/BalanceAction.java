package de.nebelniek.content.guild;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum BalanceAction {

    WITHDRAW(ChatColor.RED, "-", "Abheben"),
    DEPOSIT(ChatColor.GREEN, "+", "Einzahlen");

    private final ChatColor chatColor;
    private final String action;
    private final String prettyString;

    @Override
    public String toString() {
        return chatColor.toString();
    }

}
