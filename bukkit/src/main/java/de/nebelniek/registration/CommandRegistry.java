package de.nebelniek.registration;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import de.nebelniek.BukkitConfiguration;
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

    private final BukkitConfiguration bukkitConfiguration;

    @EventListener
    public void loadOnEnable(BukkitPluginEnableEvent event) {
        bukkitConfiguration.setCommandManager(new PaperCommandManager(event.getJavaPlugin()));
        event.getApplicationContext().getBeansOfType(BaseCommand.class).forEach((s, command) -> {
            bukkitConfiguration.getCommandManager().registerCommand(command);
            LOGGER.info("Command of bean " + s + " has been enabled!");
        });
    }

}
