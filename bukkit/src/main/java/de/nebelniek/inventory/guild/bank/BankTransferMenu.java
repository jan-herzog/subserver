package de.nebelniek.inventory.guild.bank;

import de.nebelniek.inventory.template.TemplateInventoryBackgroundProvider;
import de.nebelniek.inventory.types.GuildInventory;
import de.nebelniek.inventory.util.MenuName;

public class BankTransferMenu extends GuildInventory {

    public BankTransferMenu() {
        super(TemplateInventoryBackgroundProvider.threexnine(MenuName.BANK_TRANSFER_MENU.getName()));
    }

    @Override
    public void setup() {

    }
}
