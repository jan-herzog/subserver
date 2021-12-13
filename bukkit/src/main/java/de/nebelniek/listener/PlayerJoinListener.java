package de.nebelniek.listener;

import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.components.discord.DiscordGuildChannelService;
import de.nebelniek.components.texturehash.TextureHashProviderService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PlayerJoinListener implements Listener {

    private final TextureHashProviderService textureHashProviderService;
    private final CloudUserManagingService cloudUserManagingService;
    private final DiscordGuildChannelService discordGuildChannelService;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> {
            cloudUser.setTextureHash(textureHashProviderService.getHash(event.getPlayer()));
            if (cloudUser.getGuild() != null)
                discordGuildChannelService.createChannelsIfNotExists(cloudUser.getGuild());
            cloudUser.saveAsync();
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
}
