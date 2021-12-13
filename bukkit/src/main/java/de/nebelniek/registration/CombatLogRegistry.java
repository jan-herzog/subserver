package de.nebelniek.registration;

import de.nebelniek.components.combatlog.CombatLogService;
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
public class CombatLogRegistry {

    private final CombatLogService combatLogService;

    @EventListener
    public void loadOnEnable(BukkitPluginEnableEvent event) {
        combatLogService.start(event.getPlugin());
    }

}
