package de.nebelniek.inventory.types;

import de.nebelniek.Subserver;
import de.nebelniek.content.guild.GuildContentService;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.inventory.types.SubserverInventory;
import org.bukkit.inventory.Inventory;

public abstract class GuildInventory extends SubserverInventory {

    protected final GuildContentService guildContentService;
    protected final CloudUserManagingService cloudUserManagingService;

    public GuildInventory(Inventory inventory) {
        super(inventory);
        this.guildContentService = Subserver.getContext().getBean(GuildContentService.class);
        this.cloudUserManagingService = Subserver.getContext().getBean(CloudUserManagingService.class);
    }

}
