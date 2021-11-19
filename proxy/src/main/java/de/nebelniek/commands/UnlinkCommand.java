package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("unlink")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UnlinkCommand extends BaseCommand {

    private final CloudUserManagingService cloudUserManagingService;

    @Default
    @CatchUnknown
    public void onDefault(ProxiedPlayer sender) {
        sender.sendMessage(Prefix.LINK + "Hilfe für §b/unlink§7:");
        sender.sendMessage(Prefix.LINK + "/unlink §btwitch");
        sender.sendMessage(Prefix.LINK + "Trenne deinen Minecraft Account mit deinem §5Twitch§7-Account");
        sender.sendMessage(Prefix.LINK + "/unlink §bdiscord");
        sender.sendMessage(Prefix.LINK + "Trenne deinen Minecraft Account mit deinem §9Discord§7-Account");
    }

    @Subcommand("twitch")
    public void onTwitch(ProxiedPlayer sender) {
        if (sender.getServer().getInfo().getName().contains("Subserver")) {
            sender.connect(ProxyServer.getInstance().getServerInfo("Lobby-1"));
            sender.sendMessage(Prefix.LINK + "Da du auf dem §2Subserver §7warst wurdest du nach §eLobby-1 §7verschoben!");
        }
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> {
            if(cloudUser.getTwitchId() == null) {
                sender.sendMessage(Prefix.LINK + "Du bist mit §ckeinem §5Twitch§7-Account verbunden§7!");
                return;
            }
            cloudUser.setTwitchId(null);
            cloudUser.saveAsync();
            sender.sendMessage(Prefix.LINK + "Dein §5Twitch§7-Account wurde von deinem Minecraft Account §cgetrennt§7!");
        });
    }

    @Subcommand("discord")
    public void onDiscord(ProxiedPlayer sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> {
            if(cloudUser.getDiscordId() == null) {
                sender.sendMessage(Prefix.LINK + "Du bist mit §ckeinem §9Discord§7-Account verbunden§7!");
                return;
            }
            cloudUser.setDiscordId(null);
            cloudUser.saveAsync();
            //TODO: SEND PLUGIN MESSAGE TO BUKKIT -> DISCORD REMOVE ROLE
            sender.sendMessage(Prefix.LINK + "Dein §9Discord§7-Account wurde von deinem Minecraft Account §cgetrennt§7!");
        });
    }
}
