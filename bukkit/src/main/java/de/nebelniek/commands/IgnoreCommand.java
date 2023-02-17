package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import de.nebelniek.utils.Prefix;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("ignore")
public class IgnoreCommand extends BaseCommand {

    @Default
    public void onDefault(Player sender) {
        sender.sendMessage(Prefix.SUBSERVER + "§l§6Hilfe§7 für §e/ignore§7:");
        sender.sendMessage(Prefix.SUBSERVER + "/ignore §e[Spieler]");
        sender.sendMessage(Prefix.SUBSERVER + "/unignore §e[Spieler]");
        sender.sendMessage(Prefix.SUBSERVER + "Ignoriere/Akzeptiere einen Spieler");
    }


}
