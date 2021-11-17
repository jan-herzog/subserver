package de.nebelniek.tablistchat;

import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.tablistchat.rank.Rank;
import de.nebelniek.tablistchat.utils.NameUtils;
import de.nebelniek.utils.SubserverRank;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
            System.out.println(cloudUser.getGuild());
            if (cloudUser.getGuild() != null)
                if (cloudUser.getGuild().getPrefix() != null) {
                    Bukkit.broadcastMessage("§7" + cloudUser.getGuild().getPrefix() + " §7" + event.getPlayer().getName() + " §8»§7 " + finalMessage);
                    return;
                }
            Bukkit.broadcastMessage(SubserverRank.DEFAULT.getPrefix() + " §7" + event.getPlayer().getName() + " §8»§7 " + finalMessage);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
        event.setCancelled(true);
    }

}
