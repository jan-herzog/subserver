package de.nebelniek.inventory.template;

import de.notecho.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.springframework.stereotype.Component;

public class TemplateInventoryBackgroundProvider {

    public static Inventory fivexnine(String name) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, name);
        for (int i = 0; i < 5 * 9; i++)
            inventory.setItem(i, ItemBuilder.item(Material.WHITE_STAINED_GLASS_PANE).build());
        inventory.setItem(9, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(0, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(1, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());

        inventory.setItem(7, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(8, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(17, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());

        inventory.setItem(35, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(44, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(43, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());

        inventory.setItem(27, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(36, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(37, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());

        for (int i = 18; i < 27; i++)
            inventory.setItem(i, ItemBuilder.item(Material.RED_STAINED_GLASS_PANE).build());
        inventory.setItem(4, ItemBuilder.item(Material.RED_STAINED_GLASS_PANE).build());
        inventory.setItem(13, ItemBuilder.item(Material.RED_STAINED_GLASS_PANE).build());
        inventory.setItem(31, ItemBuilder.item(Material.RED_STAINED_GLASS_PANE).build());
        inventory.setItem(40, ItemBuilder.item(Material.RED_STAINED_GLASS_PANE).build());

        return inventory;
    }

    public static Inventory threexnine(String name) {
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, name);
        inventory.setItem(9, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(0, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(1, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());

        inventory.setItem(7, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(8, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(17, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());

        inventory.setItem(18, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(19, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());

        inventory.setItem(26, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());
        inventory.setItem(25, ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).build());

        for (int i = 10; i < 17; i++)
            inventory.setItem(i, ItemBuilder.item(Material.RED_STAINED_GLASS_PANE).build());
        inventory.setItem(4, ItemBuilder.item(Material.RED_STAINED_GLASS_PANE).build());
        inventory.setItem(22, ItemBuilder.item(Material.RED_STAINED_GLASS_PANE).build());
        return inventory;
    }


}
