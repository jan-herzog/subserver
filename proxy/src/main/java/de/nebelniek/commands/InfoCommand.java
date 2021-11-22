package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@CommandAlias("info|i")
@CommandPermission("proxy.info")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InfoCommand extends BaseCommand {

    private final CloudUserManagingService cloudUserManagingService;

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM - HH:mm:ss");

    @Default
    @CatchUnknown
    public void onHelp(ProxiedPlayer sender) {
        sender.sendMessage(Prefix.LINK + "Hilfe für §c/info§7:");
        sender.sendMessage(Prefix.LINK + "/info §c[User]");
        sender.sendMessage(Prefix.LINK + "Information über einen bestimmten §cCloudUser");
    }

    @Default

    public void onInfo(ProxiedPlayer sender, @Single String targetName) {
        cloudUserManagingService.loadUserByName(targetName).thenAccept(cloudUser -> {
            if (cloudUser == null) {
                sender.sendMessage(Prefix.PROXY + "Dieser Spieler ist §cnicht§7 in der Datenbank eingetragen!");
                return;
            }

            sender.sendMessage(Prefix.PROXY + "§7Info für §8» §e" + cloudUser.getLastUserName());
            sender.sendMessage(Prefix.PROXY + "§7UUID §8» §a" + cloudUser.getUuid().toString());
            sender.sendMessage(Prefix.PROXY + "§7Letzter Login §8» §a" + formatter.format(cloudUser.getLastLogin()));
            sender.sendMessage(Prefix.PROXY + "§7Aktuell Online §8» §a" + (ProxyServer.getInstance().getPlayer(targetName) != null ? "§aOnline" : "§cOffline"));
            sender.sendMessage(Prefix.PROXY + "§7Gilden Rolle §8» §a" + (cloudUser.getGuildRole() == null ? "§cnull" : cloudUser.getGuildRole().getPrettyName()));
            sender.sendMessage(Prefix.PROXY + "§7Gilde §8» §a" + (cloudUser.getGuild() == null ? "§cnull" : cloudUser.getGuild().getName() + "§7 (§4" + cloudUser.getGuild().getOwner().getLastUserName() + "§7)"));
            sender.sendMessage(Prefix.PROXY + "§7Coins §8» §a" + cloudUser.getCoins());
            sender.sendMessage(Prefix.PROXY + "§7Twitch Verbunden §8» §a" + (cloudUser.getTwitchId() != null ? "§aVerbunden" : "§cNicht Verbunden") + "§7(" + cloudUser.getTwitchId() + "§7)!");
            sender.sendMessage(Prefix.PROXY + "§7Discord Verbunden §8» §a" + (cloudUser.getDiscordId() != null ? "§aVerbunden" : "§cNicht Verbunden") + "§7(" + cloudUser.getDiscordId() + "§7)!");
            sender.sendMessage(Prefix.PROXY + "§7Twitch-Subscriber §8» " + (cloudUser.isSubbed() ? "§atrue" : "§cfalse"));
        });
    }

}
