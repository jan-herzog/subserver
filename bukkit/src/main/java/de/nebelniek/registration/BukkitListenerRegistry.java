package de.nebelniek.registration;

import org.bukkit.event.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class BukkitListenerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(BukkitListenerRegistry.class);

    @EventListener
    public void loadOnEnable(BukkitPluginEnableEvent event) {
        event.getApplicationContext().getBeansOfType(Listener.class).forEach((s, listener) -> {
            event.getPluginManager().registerEvents(listener, event.getJavaPlugin());
            LOGGER.info("Listener of bean " + s + " has been enabled!");
        });
    }

}
