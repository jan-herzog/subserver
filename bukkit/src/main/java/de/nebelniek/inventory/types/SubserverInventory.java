package de.nebelniek.inventory.types;

import de.nebelniek.Subserver;
import de.notecho.inventory.InventoryManager;
import de.notecho.inventory.inventories.BaseInventory;
import org.bukkit.inventory.Inventory;

public abstract class SubserverInventory extends BaseInventory {

    public SubserverInventory(Inventory inventory) {
        super(inventory);
        Subserver.getContext().getBean(InventoryManager.class).registerInventory(this);
    }

}
