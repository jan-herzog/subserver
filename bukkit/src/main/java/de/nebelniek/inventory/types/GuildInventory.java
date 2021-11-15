package de.nebelniek.inventory.types;

import de.nebelniek.Subserver;
import de.nebelniek.content.guild.GuildContentService;
import de.nebelniek.content.guild.response.GuildContentResponse;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.utils.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class GuildInventory extends SubserverInventory {

    protected final GuildContentService guildContentService;
    protected final CloudUserManagingService cloudUserManagingService;
    protected final IGuild guild;

    public GuildInventory(Inventory inventory) {
        super(inventory);
        this.guildContentService = Subserver.getContext().getBean(GuildContentService.class);
        this.cloudUserManagingService = Subserver.getContext().getBean(CloudUserManagingService.class);
        this.guild = null;
    }


    public GuildInventory(Inventory inventory, IGuild guild) {
        super(inventory);
        this.guild = guild;
        this.guildContentService = Subserver.getContext().getBean(GuildContentService.class);
        this.cloudUserManagingService = Subserver.getContext().getBean(CloudUserManagingService.class);
    }


    protected void sendResponse(Player player, GuildContentResponse response) {
        switch (response.state()) {
            case ERROR -> player.sendMessage(Prefix.GUILD + "§cFehler§7: §c" + response.message());
            case SUCCESS -> player.sendMessage(Prefix.GUILD + response.message());
        }
    }

}
