package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.services.verify.DiscordVerifyService;
import de.nebelniek.services.verify.TwitchVerifyService;
import de.nebelniek.services.verify.VerifyService;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("verify")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class VerifyCommand extends BaseCommand {

    private final CloudUserManagingService cloudUserRepository;

    private final TwitchVerifyService twitchVerifyService;

    private final DiscordVerifyService discordVerifyService;

    @Default
    @CatchUnknown
    public void onDefault(ProxiedPlayer sender) {
        sender.sendMessage(Prefix.LINK + "Hilfe für §b/verify§7:");
        sender.sendMessage(Prefix.LINK + "/verify §btwitch");
        sender.sendMessage(Prefix.LINK + "Verbinde dich mit deinem §5Twitch§7-Account");
        sender.sendMessage(Prefix.LINK + "/verify §bdiscord");
        sender.sendMessage(Prefix.LINK + "Verbinde dich mit deinem §9Discord§7-Account");
    }

    @Subcommand("twitch")
    public void onTwitch(ProxiedPlayer sender) {
        cloudUserRepository.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> {
            if(cloudUser.getTwitchId() == null)
                twitchVerifyService.showVerify(sender);
            else sender.sendMessage(Prefix.TWITCH + "Du bist bereits §averifiziert§7!");
        });
    }

    @Subcommand("discord")
    public void onDiscord(ProxiedPlayer sender) {
        if(sender.getServer().getInfo().getName().contains("Subserver")) {
            sender.sendMessage(Prefix.DISCORD + "Um dich mit §9Discord§7 zu verbinden, gehe bitte auf die Lobby. (§e/l§7)");
            return;
        }
        cloudUserRepository.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> {
            if(cloudUser.getDiscordId() == null)
                discordVerifyService.showVerify(sender);
            else sender.sendMessage(Prefix.DISCORD + "Du bist bereits §averlinkt§7!");
        });
    }
}
