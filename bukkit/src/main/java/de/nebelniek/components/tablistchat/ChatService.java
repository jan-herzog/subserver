package de.nebelniek.components.tablistchat;

import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.components.tablistchat.rank.Rank;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.SubserverRank;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ChatService implements Listener {

    private final CloudUserManagingService cloudUserManagingService;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Rank rank = Rank.getRank(event.getPlayer());
        String message = event.getMessage().replace("%", " Prozent");
        if (rank.isHigherThanOrEquals(Rank.ADMIN))
            message = ChatColor.translateAlternateColorCodes('&', message);
        String finalMessage = message;
        cloudUserManagingService.getUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> {
            String messageToBroadcast = finalMessage;
            if (cloudUser.getGuild() != null)
                if (cloudUser.getGuild().getPrefix() != null) {
                    messageToBroadcast = "§7" + cloudUser.getGuild().getPrefix() + " " + cloudUser.getGuild().getColor() + event.getPlayer().getName() + " §8»§7 " + finalMessage;
                    return;
                }
            messageToBroadcast = SubserverRank.DEFAULT.getPrefix() + " §7" + event.getPlayer().getName() + " §8»§7 " + finalMessage;
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                ICloudUser onlineCloudUser = cloudUserManagingService.getCloudUsers().get(onlinePlayer.getUniqueId());
                if (!onlineCloudUser.getIgnored().contains(cloudUser))
                    onlinePlayer.sendMessage(messageToBroadcast);
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
        event.setCancelled(true);
    }

}
