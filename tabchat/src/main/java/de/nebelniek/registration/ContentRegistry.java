package de.nebelniek.registration;

import de.nebelniek.application.ApplicationServiceMode;
import de.nebelniek.content.tablist.TablistService;
import de.nebelniek.content.tablist.TablistServiceDefault;
import de.nebelniek.content.tablist.TablistServiceSubserver;
import de.nebelniek.registration.event.BukkitPluginEnableEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ContentRegistry {

    private final ApplicationServiceMode applicationServiceMode;

    private static final Logger LOGGER = LoggerFactory.getLogger(BukkitListenerRegistry.class);

    @EventListener
    public void loadOnEnable(BukkitPluginEnableEvent event) {
        TablistService tablistService = applicationServiceMode.equals(ApplicationServiceMode.SUBSERVER) ? event.getApplicationContext().getBean(TablistServiceSubserver.class) : event.getApplicationContext().getBean(TablistServiceDefault.class);
        Bukkit.getPluginManager().registerEvents((Listener) tablistService, event.getPlugin());
        tablistService.createTeams();
        LOGGER.info("TablistService of bean " + tablistService.getClass().getName() + " has been enabled!");
    }

}
