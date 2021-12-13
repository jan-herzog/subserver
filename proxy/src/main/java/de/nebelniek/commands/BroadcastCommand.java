package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import de.nebelniek.utils.Prefix;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("broadcast|br")
@CommandPermission("proxy.boradcast")
public class BroadcastCommand extends BaseCommand {

    @Default
    public void onDefault(ProxiedPlayer sender, String text) {
        ProxyServer.getInstance().broadcast(Prefix.BROADCAST + ChatColor.translateAlternateColorCodes('&', text));
    }

    @Default
    public void onHelp(ProxiedPlayer sender) {
        sender.sendMessage(Prefix.PROXY + "Hilfe für §e/broadcast§7:");
        sender.sendMessage(Prefix.PROXY + "/br §6[Text]");
        sender.sendMessage(Prefix.PROXY + "Schicke eine Nachricht über alle Server.");
    }

}
