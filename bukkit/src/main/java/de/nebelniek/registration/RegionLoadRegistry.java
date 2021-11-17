package de.nebelniek.registration;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.registration.event.BukkitPluginEnableEvent;
import de.nebelniek.registration.event.GuildsLoadedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RegionLoadRegistry {

    private final GuildManagingService guildManagingService;

    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void loadOnEnable(BukkitPluginEnableEvent event) {
        guildManagingService.loadGuilds().thenAccept(v -> {
            eventPublisher.publishEvent(new GuildsLoadedEvent(event.getApplicationContext()));
        });
    }

}
