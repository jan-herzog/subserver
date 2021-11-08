package de.nebelniek.inventory;

import de.notecho.inventory.click.OptionHandler;
import de.notecho.inventory.inventories.BaseInventory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class GuildMainMenu extends BaseInventory {
    public GuildMainMenu() {
        super(Bukkit.createInventory(null, 27, "§8» §2§lGilden §r§8«"));
    }

    @Override
    public void setup() {



    }

}
