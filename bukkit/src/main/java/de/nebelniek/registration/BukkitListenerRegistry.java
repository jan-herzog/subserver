package de.nebelniek.registration;

import de.nebelniek.registration.event.BukkitPluginEnableEvent;
import de.nebelniek.components.tablistchat.TablistServiceSubserver;
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
public class BukkitListenerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(BukkitListenerRegistry.class);

    private final TablistServiceSubserver tablistServiceSubserver;

    @EventListener
    public void loadOnEnable(BukkitPluginEnableEvent event) {
        event.getApplicationContext().getBeansOfType(Listener.class).forEach((s, listener) -> {
            if (!listener.getClass().equals(TablistServiceSubserver.class)) {
                Bukkit.getPluginManager().registerEvents(listener, event.getPlugin());
                LOGGER.info("Listener of bean " + s + " has been enabled!");
            } else TablistServiceSubserver.setInstance(tablistServiceSubserver);
        });
    }

}
