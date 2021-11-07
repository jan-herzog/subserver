package de.nebelniek.content.guild.chat;

import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Service;

@Service
public class GuildChatService {

    public void sendAnnouncement(IGuild guild, String message) {
        for (ICloudUser iCloudUser : guild.getMember()) {
            Player player = Bukkit.getPlayer(iCloudUser.getUuid());
            if (player == null)
                continue;
            player.sendMessage(Prefix.GUILD + message);
        }
    }

    public void sendMessage(IGuild guild, ICloudUser sender, String message) {
        for (ICloudUser iCloudUser : guild.getMember()) {
            Player player = Bukkit.getPlayer(iCloudUser.getUuid());
            if (player == null)
                continue;
            player.sendMessage(Prefix.GUILDCHAT + sender.getGuildRole().getColor() + sender.getLastUserName() + "§7: " + message);
        }
    }
}
