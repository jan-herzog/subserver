package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.nebelniek.content.coins.CoinsContentService;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("coins")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CoinsCommand extends BaseCommand {

    private final CloudUserManagingService cloudUserManagingService;

    private final CoinsContentService coinsContentService;

    @Default
    @CatchUnknown
    public void onDefault(Player sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> {
            sender.sendMessage(Prefix.COINS + "Deine Coins: §e" + cloudUser.getCoins());
        });
    }

    @Subcommand("set")
    @CommandPermission("coins.set")
    public void set(Player sender, @Single String playerName, long value) {
        cloudUserManagingService.loadUserByName(playerName).thenAccept(cloudUser -> {
            if (cloudUser == null) {
                sender.sendMessage(Prefix.COINS + "§cDieser Spieler existiert nicht in der Datenbank!");
                return;
            }
            cloudUser.setCoins(value);
            cloudUser.saveAsync();
            sender.sendMessage(Prefix.COINS + "Du hast die §eCoins§7 von §a" + playerName + "§7 auf §e" + value + " §7gesetzt!");
        });
    }

}
