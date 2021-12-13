package de.nebelniek.registration;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import de.nebelniek.configuration.BukkitConfiguration;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.registration.CommandRegistry;
import de.nebelniek.registration.event.BukkitPluginEnableEvent;
import de.notecho.inventory.InventoryManager;
import de.notecho.inventory.NotechoInventory;
import de.notecho.inventory.inventories.BaseInventory;
import de.notecho.inventory.inventories.SiteInventory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InventoryRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandRegistry.class);

    private final BukkitConfiguration bukkitConfiguration;

    @EventListener
    public void loadOnEnable(BukkitPluginEnableEvent event) {
        bukkitConfiguration.setInventoryManager(new InventoryManager(event.getPlugin()));
        event.getApplicationContext().getBeansOfType(BaseInventory.class).forEach((s, inventory) -> {
            bukkitConfiguration.getInventoryManager().registerInventory(inventory);
            LOGGER.info("BaseInventory of bean " + s + " has been enabled!");
        });
        event.getApplicationContext().getBeansOfType(SiteInventory.class).forEach((s, inventory) -> {
            bukkitConfiguration.getInventoryManager().registerInventory(inventory);
            LOGGER.info("SiteInventory of bean " + s + " has been enabled!");
        });
    }
}
