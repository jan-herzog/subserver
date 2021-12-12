package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("load")
@CommandPermission("server.load")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoadCommand extends BaseCommand {

    private final GuildManagingService guildManagingService;

    private final CloudUserManagingService cloudUserManagingService;

    @Subcommand("guild")
    public void onGuild(Player sender, int guildId) {
        guildManagingService.getGuildById(guildId).loadAsync();
        sender.sendMessage(Prefix.SUBSERVER + "§e" + guildId + " §7reloaded!");
    }

    @Subcommand("clouduser")
    public void onCloudUser(Player sender, int userId) {
        cloudUserManagingService.loadUserByIdSync(userId);
        sender.sendMessage(Prefix.SUBSERVER + "§a" + userId + " §7reloaded!");
    }
}
