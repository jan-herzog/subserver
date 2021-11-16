package de.nebelniek.registration;

import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.registration.event.BukkitPluginEnableEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuildRegistry {

    private final GuildManagingService guildManagingService;

    @EventListener
    public void loadOnEnable(BukkitPluginEnableEvent event) {
        guildManagingService.loadGuilds();
    }

}
