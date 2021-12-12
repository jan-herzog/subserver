package de.nebelniek.content.guild.chat;

import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.util.GuildRole;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

    public void sendAnnouncementToRole(IGuild guild, GuildRole guildRole, Component component) {
        for (ICloudUser iCloudUser : guild.getMember()) {
            Player player = Bukkit.getPlayer(iCloudUser.getUuid());
            if (player == null)
                continue;
            player.sendMessage(Component.text(Prefix.GUILD.getPrefix()).append(component));
        }
    }

    public void sendAnnouncementToRole(IGuild guild, GuildRole guildRole, String message) {
        for (ICloudUser iCloudUser : guild.getMember()) {
            Player player = Bukkit.getPlayer(iCloudUser.getUuid());
            if (player == null)
                continue;
            player.sendMessage(Component.text(Prefix.GUILD.getPrefix()).append(Component.text(message)));
        }
    }

    public void sendMessage(IGuild guild, ICloudUser sender, String message) {
        for (ICloudUser iCloudUser : guild.getMember()) {
            Player player = Bukkit.getPlayer(iCloudUser.getUuid());
            if (player == null)
                continue;
            player.sendMessage(Prefix.GUILDCHAT + sender.getGuildRole().getColor() + sender.getLastUserName() + (guild.getAllies().size() == 0 ? " " : "§7(" + guild.getColor() + guild.getName() + "§7) ") + ": " + message);
        }
        if (guild.getAllies().size() != 0) {
            for (IGuild ally : guild.getAllies()) {
                for (ICloudUser iCloudUser : ally.getMember()) {
                    Player player = Bukkit.getPlayer(iCloudUser.getUuid());
                    if (player == null)
                        continue;
                    player.sendMessage(Prefix.GUILDCHAT + sender.getGuildRole().getColor() + sender.getLastUserName() + "§7(" + guild.getColor() + guild.getName() + "§7) " + ": " + message);
                }
            }
        }
    }

    public boolean someoneOnline(IGuild guild) {
        for (ICloudUser iCloudUser : guild.getMember()) {
            Player player = Bukkit.getPlayer(iCloudUser.getUuid());
            if (player == null)
                continue;
            return true;
        }
        return false;
    }
}
