package de.nebelniek.inventory.types;

import de.nebelniek.Subserver;
import de.nebelniek.configuration.BukkitConfiguration;
import de.notecho.inventory.InventoryManager;
import de.notecho.inventory.inventories.BaseInventory;
import org.bukkit.inventory.Inventory;

public abstract class SubserverInventory extends BaseInventory {

    public SubserverInventory(Inventory inventory) {
        super(inventory);
        Subserver.getContext().getBean(BukkitConfiguration.class).getInventoryManager().registerInventory(this);
    }

}
