package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import de.nebelniek.content.guild.GuildContentService;
import de.nebelniek.content.guild.response.GuildContentResponse;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("spawn")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SpawnCommand extends BaseCommand {

    private final GuildContentService guildContentService;

    private final CloudUserManagingService cloudUserManagingService;

    @Default
    public void onDefault(Player sender) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        sendResponse(sender, guildContentService.tpSpawn(cloudUser, sender));
    }

    private void sendResponse(Player player, GuildContentResponse response) {
        switch (response.state()) {
            case ERROR -> player.sendMessage(Prefix.GUILD + "§cFehler§7: §c" + response.message());
            case SUCCESS -> player.sendMessage(Prefix.GUILD + response.message());
        }
    }

}
