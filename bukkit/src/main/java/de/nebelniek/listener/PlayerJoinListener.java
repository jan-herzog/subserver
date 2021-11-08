package de.nebelniek.listener;

import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.texturehash.TextureHashProviderService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PlayerJoinListener implements Listener {

    private final TextureHashProviderService textureHashProviderService;
    private final CloudUserManagingService cloudUserManagingService;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> {
            cloudUser.setTextureHash(textureHashProviderService.getHash(event.getPlayer()));
            cloudUser.saveAsync();
        });
    }
}
