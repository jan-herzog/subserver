package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import de.nebelniek.utils.Prefix;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("info|i")
@CommandPermission("proxy.info")
public class InfoCommand extends BaseCommand {

    @Default
    @CatchUnknown
    public void onHelp(ProxiedPlayer sender) {
        sender.sendMessage(Prefix.LINK + "Hilfe für §c/info§7:");
        sender.sendMessage(Prefix.LINK + "/info §c[User]");
        sender.sendMessage(Prefix.LINK + "Information über einen bestimmten §cCloudUser");
    }

}
