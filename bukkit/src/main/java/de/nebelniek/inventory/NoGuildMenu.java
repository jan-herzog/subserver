package de.nebelniek.inventory;

import de.nebelniek.inventory.template.TemplateInventoryBackgroundProvider;
import de.nebelniek.inventory.util.MenuName;
import de.notecho.ItemBuilder;
import de.notecho.inventory.click.ClickOption;
import de.notecho.inventory.inventories.BaseInventory;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class NoGuildMenu extends BaseInventory {
    public NoGuildMenu() {
        super(TemplateInventoryBackgroundProvider.threexnine(MenuName.NOGUILD_MAIN_MENU.getName()));
    }

    @Override
    public void setup() {

        addClickOption(new ClickOption(4, ItemBuilder.item(Material.RAW)));

    }
}
