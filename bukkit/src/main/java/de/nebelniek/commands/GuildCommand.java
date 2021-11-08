package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.nebelniek.content.guild.GuildContentService;
import de.nebelniek.content.guild.chat.GuildChatService;
import de.nebelniek.content.guild.response.GuildContentResponse;
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

    private final GuildContentService service;

    private final GuildChatService guildChatService;

    @Default
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
    @CatchUnknown
    public void help(Player sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> {
            sender.sendMessage(Prefix.GUILD + "Hilfe für §a/guild§7:");
            sender.sendMessage(Prefix.GUILD + "/guild");
            sender.sendMessage(Prefix.GUILD + "Öffnet das Guild-Menu");
            sender.sendMessage(Prefix.GUILD + "/guild §acreate§2 [name]");
            sender.sendMessage(Prefix.GUILD + "Erstellt eine Gilde | Kosten: 10k");
            if (cloudUser.getGuild() != null) {
                sender.sendMessage(Prefix.GUILD + "/guild §arename§2 [name]");
                sender.sendMessage(Prefix.GUILD + "Nenne deine Gilde um | Kosten: 5k");
                sender.sendMessage(Prefix.GUILD + "/guild §achangeprefix§2 [prefix]");
                sender.sendMessage(Prefix.GUILD + "Setzt den Prefix | Kosten: 10k");
                sender.sendMessage(Prefix.GUILD + "/guild §achat§2 [Nachricht]");
                sender.sendMessage(Prefix.GUILD + "Alias: §a/gc");
                sender.sendMessage(Prefix.GUILD + "Sende deinen Kollegen eine Nachricht");
            }
        });
    }

    @Subcommand("create")
    public void create(Player sender, @Single String name) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.createGuild(cloudUser, name)));
    }

    @Subcommand("rename")
    public void rename(Player sender, @Single String name) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.renameGuild(cloudUser, name)));
    }

    @Subcommand("changeprefix")
    public void changePrefix(Player sender, @Single String prefix) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.changePrefix(cloudUser, prefix)));
    }

    @Subcommand("chat")
    @CommandAlias("gc")
    public void chat(Player sender, String message) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> guildChatService.sendMessage(cloudUser.getGuild(), cloudUser, message));
    }

    private void sendResponse(Player player, GuildContentResponse response) {
        switch (response.state()) {
            case ERROR -> player.sendMessage(Prefix.GUILD + "§cFehler§7: §c" + response.message());
            case SUCCESS -> player.sendMessage(Prefix.GUILD + response.message());
        }
    }

}
