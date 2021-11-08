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
@CommandAlias("pay")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PayCommand extends BaseCommand {

    private final CloudUserManagingService cloudUserManagingService;

    private final CoinsContentService coinsContentService;

    @Default
    @CatchUnknown
    public void onHelp(Player sender) {
        sender.sendMessage(Prefix.COINS + "Hilfe für §e/pay§7:");
        sender.sendMessage(Prefix.COINS + "/pay §e[Spieler] [Wert]");
        sender.sendMessage(Prefix.COINS + "Sende jemandem Geld");
    }

    @Default
    public void onDefault(Player sender, @Single String target, long amount) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> {
            cloudUserManagingService.loadUserByName(target).thenAccept(targetUser -> {
                if (targetUser == null) {
                    sender.sendMessage(Prefix.COINS + "§cDieser Spieler ist nicht in der Datenbank eingetragen!");
                    return;
                }
                if (cloudUser.getCoins() < amount) {
                    sender.sendMessage(Prefix.COINS + "§cDu hast zu wenig Geld!");
                    return;
                }
                coinsContentService.removeCoins(cloudUser, amount);
                coinsContentService.addCoins(targetUser, amount);
                sender.sendMessage(Prefix.COINS + "Du hast §5" + targetUser.getLastUserName() + " §e" + amount + "§7 Coins geschickt!");
            });
        });
    }

}
