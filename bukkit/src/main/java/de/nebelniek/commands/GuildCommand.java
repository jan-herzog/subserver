package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("guild")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuildCommand extends BaseCommand {

    private final CloudUserManagingService cloudUserManagingService;

    @Default
    @CatchUnknown
    public void onDefault(Player sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> {
            if (cloudUser.getGuild() != null) {
                //TODO: Open Gui
                return;
            }
            help(sender);
        });
    }

    @Subcommand("help")
    public void help(Player sender) {
        sender.sendMessage(Prefix.GUILD + "Hilfe für §a/guild§7:");
        sender.sendMessage(Prefix.GUILD + "/guild §acreate§2 [name]");
        sender.sendMessage(Prefix.GUILD + "Erstellt eine Gilde | Kosten: 10k");
        sender.sendMessage(Prefix.GUILD + "/guild §arename§2 [name]");
        sender.sendMessage(Prefix.GUILD + "Nenne deine Gilde um | Kosten: 5k");
        sender.sendMessage(Prefix.GUILD + "/guild §achangeprefix§2 [prefix]");
        sender.sendMessage(Prefix.GUILD + "Setzt den Prefix | Kosten: 10k");
    }
}
