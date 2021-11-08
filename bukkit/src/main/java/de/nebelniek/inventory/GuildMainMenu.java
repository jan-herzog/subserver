package de.nebelniek.inventory;

import de.nebelniek.inventory.template.TemplateInventoryBackgroundProvider;
import de.notecho.inventory.inventories.BaseInventory;

import org.springframework.stereotype.Component;

@Component
public class GuildMainMenu extends BaseInventory {

    public GuildMainMenu() {
        super(TemplateInventoryBackgroundProvider.fivexnine("§8» §2§lGilden §r§8«"));
    }

    @Override
    public void setup() {

    }

}
