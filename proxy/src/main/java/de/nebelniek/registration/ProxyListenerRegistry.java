package de.nebelniek.registration;

import de.nebelniek.database.service.GuildManagingService;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProxyListenerRegistry {

    private final GuildManagingService guildManagingService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyListenerRegistry.class);

    @EventListener
    public void loadOnEnable(ProxyPluginEnableEvent event) {
        event.getApplicationContext().getBeansOfType(Listener.class).forEach((s, listener) -> {
            ProxyServer.getInstance().getPluginManager().registerListener(event.getPlugin(), listener);
            LOGGER.info("Listener of bean " + s + " has been enabled!");
        });
        guildManagingService.loadGuilds();
    }

}
