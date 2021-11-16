package de.nebelniek.content.chat;

import de.nebelniek.application.ApplicationServiceMode;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.rank.Rank;
import de.nebelniek.utils.NameUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ChatService implements Listener {

    private ApplicationServiceMode applicationServiceMode;

    private CloudUserManagingService cloudUserManagingService;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Rank rank = Rank.getRank(event.getPlayer());
        String message = event.getMessage().replace("%", " Prozent");
        if (rank.isHigherThanOrEquals(Rank.ADMIN))
            message = ChatColor.translateAlternateColorCodes('&', message);
        String name = NameUtils.getPrefix(event.getPlayer().getUniqueId()) + " §7" + event.getPlayer().getName();
        if (applicationServiceMode.equals(ApplicationServiceMode.SUBSERVER)) {
            ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(event.getPlayer().getUniqueId());
            if (cloudUser.getGuild() != null)
                if (cloudUser.getGuild().getPrefix() != null)
                    name = cloudUser.getGuild().getPrefix() + " §7" + event.getPlayer().getName();
        }
        event.setFormat(name + " §8»§7 " + message);
    }

}
