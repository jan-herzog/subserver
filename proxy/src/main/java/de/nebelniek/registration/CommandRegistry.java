package de.nebelniek.registration;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BungeeCommandManager;
import de.nebelniek.ProxyConfiguration;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommandRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandRegistry.class);

    private final ProxyConfiguration proxyConfiguration;

    @EventListener
    public void loadOnEnable(ProxyPluginEnableEvent event) {
        proxyConfiguration.setCommandManager(new BungeeCommandManager(event.getPlugin()));
        event.getApplicationContext().getBeansOfType(BaseCommand.class).forEach((s, command) -> {
            proxyConfiguration.getCommandManager().registerCommand(command);
            LOGGER.info("Command of bean " + s + " has been enabled!");
        });
    }

}
