package de.nebelniek.content.chat;

import de.nebelniek.application.ApplicationServiceMode;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.rank.Rank;
import de.nebelniek.utils.NameUtils;
import de.nebelniek.utils.SubserverRank;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ChatService implements Listener {

    private final ApplicationServiceMode applicationServiceMode;

    private final CloudUserManagingService cloudUserManagingService;
    private final GuildManagingService guildManagingService;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Rank rank = Rank.getRank(event.getPlayer());
        String message = event.getMessage().replace("%", " Prozent");
        if (rank.isHigherThanOrEquals(Rank.ADMIN))
            message = ChatColor.translateAlternateColorCodes('&', message);

        String name = NameUtils.getPrefix(event.getPlayer().getUniqueId()) + " §7" + event.getPlayer().getName();

        if (applicationServiceMode.equals(ApplicationServiceMode.SUBSERVER)) {
            String finalMessage = message;
            cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> {
                System.out.println(cloudUser.getGuild());
                if (cloudUser.getGuild() != null)
                    if (cloudUser.getGuild().getPrefix() != null) {
                        Bukkit.broadcastMessage(cloudUser.getGuild().getPrefix() + " §7" + event.getPlayer().getName() + " §8»§7 " + finalMessage);
                        return;
                    }
                Bukkit.broadcastMessage(SubserverRank.DEFAULT.getPrefix() + " §7" + event.getPlayer().getName() + " §8»§7 " + finalMessage);
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
            event.setCancelled(true);
            return;
        }
        event.setFormat(name + " §8»§7 " + message);
    }

}
