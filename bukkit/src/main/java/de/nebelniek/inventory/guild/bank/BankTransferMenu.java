package de.nebelniek.inventory.guild.bank;

import de.nebelniek.content.guild.BalanceAction;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.inventory.guild.GuildMainMenu;
import de.nebelniek.inventory.template.TemplateInventoryBackgroundProvider;
import de.nebelniek.inventory.types.GuildInventory;
import de.nebelniek.inventory.util.ItemColors;
import de.nebelniek.inventory.util.MenuName;
import de.nebelniek.utils.Prefix;
import de.notecho.ItemBuilder;
import de.notecho.inventory.click.ClickOption;
import de.notecho.inventory.click.OptionClickEvent;
import de.notecho.inventory.click.OptionExecutor;
import de.notecho.inventory.click.OptionHandler;
import org.bukkit.Material;

public class BankTransferMenu extends GuildInventory {

    private final BalanceAction balanceAction;

    public BankTransferMenu(IGuild guild, BalanceAction balanceAction, ICloudUser opener) {
        super(TemplateInventoryBackgroundProvider.threexnine(MenuName.BANK_TRANSFER_MENU.getName()), guild, opener);
        this.balanceAction = balanceAction;
        setup();
    }

    @Override
    public void setup() {
        getInventory().setItem(4, ItemBuilder.item(Material.RAW_GOLD_BLOCK)
                .setDisplayName("§8» " + ItemColors.BANK.getPrimary() + "§lBank §r§8«")
                .setLore(
                        " §7➥ Aktion§7 ➞ " + balanceAction + balanceAction.getPrettyString(),
                        " §7➥ " + ItemColors.BANK.getPrimary() + "Kontostand§7 ➞ " + ItemColors.BANK.getAccent() + this.guild.getBalance(),
                        " §7➥ §aBenutzerdefinierte Mengen§7 ➞ Benutze dazu bitte §a/guild bank " + balanceAction.name().toLowerCase() + " §2[Wert]"
                )
                .build());
        addClickOption(new ClickOption(11, ItemBuilder.item(Material.GOLD_NUGGET)
                .setDisplayName("§7" + balanceAction.getAction() + " " + balanceAction + "100")
                .setLore(
                        balanceAction.equals(BalanceAction.DEPOSIT) ?
                                " §7➥ §aLinksklick§7 ➞ §6100 §alagern" :
                                " §7➥ §cRechtsklick§7 ➞ §6100 §cabheben"
                )
                .build()));
        addClickOption(new ClickOption(13, ItemBuilder.item(Material.GOLD_INGOT)
                .setDisplayName("§7" + balanceAction.getAction() + " " + balanceAction + "1000")
                .setLore(
                        balanceAction.equals(BalanceAction.DEPOSIT) ?
                                " §7➥ §aLinksklick§7 ➞ §61000 §alagern" :
                                " §7➥ §cRechtsklick§7 ➞ §61000 §cabheben"
                )
                .build()));
        addClickOption(new ClickOption(15, ItemBuilder.item(Material.RAW_GOLD)
                .setDisplayName("§7" + balanceAction.getAction() + " " + balanceAction + "10000")
                .setLore(
                        balanceAction.equals(BalanceAction.DEPOSIT) ?
                                " §7➥ §aLinksklick§7 ➞ §610000 §alagern" :
                                " §7➥ §cRechtsklick§7 ➞ §610000 §cabheben"
                )
                .build()));
        addClickOption(new ClickOption(26, ItemBuilder.item(Material.GREEN_STAINED_GLASS_PANE)
                .setDisplayName("§8» §c§lZurück §r§8«")
                .build()));
        addOptionExecutor(new OptionExecutor(26, event -> new GuildMainMenu(guild, opener).open(event.getPlayer())));
    }

    @OptionHandler(11)
    public void on100(OptionClickEvent event) {
        switch (balanceAction) {
            case DEPOSIT -> event.getPlayer().sendMessage(Prefix.COINS + "Du hast " + ItemColors.BANK.getPrimary() + "100§7$ an deine" + ItemColors.BANK.getAccent() + " Gilden-Bank §aeingezahlt§7.");
            case WITHDRAW -> event.getPlayer().sendMessage(Prefix.COINS + "Du hast " + ItemColors.BANK.getPrimary() + "100§7$ aus deiner" + ItemColors.BANK.getAccent() + " Gilden-Bank §causgezahlt§7.");
        }
        guildContentService.changeBalance(opener, 100, balanceAction);
        updateValue();
    }

    @OptionHandler(13)
    public void on1000(OptionClickEvent event) {
        switch (balanceAction) {
            case DEPOSIT -> event.getPlayer().sendMessage(Prefix.COINS + "Du hast " + ItemColors.BANK.getPrimary() + "1000§7$ an deine" + ItemColors.BANK.getAccent() + " Gilden-Bank §aeingezahlt§7.");
            case WITHDRAW -> event.getPlayer().sendMessage(Prefix.COINS + "Du hast " + ItemColors.BANK.getPrimary() + "1000§7$ aus deiner" + ItemColors.BANK.getAccent() + " Gilden-Bank §causgezahlt§7.");
        }
        guildContentService.changeBalance(opener, 1000, balanceAction);
        updateValue();
    }

    @OptionHandler(15)
    public void on10000(OptionClickEvent event) {
        switch (balanceAction) {
            case DEPOSIT -> event.getPlayer().sendMessage(Prefix.COINS + "Du hast " + ItemColors.BANK.getPrimary() + "10000§7$ an deine" + ItemColors.BANK.getAccent() + " Gilden-Bank §aeingezahlt§7.");
            case WITHDRAW -> event.getPlayer().sendMessage(Prefix.COINS + "Du hast " + ItemColors.BANK.getPrimary() + "10000§7$ aus deiner" + ItemColors.BANK.getAccent() + " Gilden-Bank §causgezahlt§7.");
        }
        guildContentService.changeBalance(opener, 10000, balanceAction);
        updateValue();
    }

    private void updateValue() {
        getInventory().setItem(4, ItemBuilder.item(Material.RAW_GOLD_BLOCK)
                .setDisplayName("§8» " + ItemColors.BANK.getPrimary() + "§lBank §r§8«")
                .setLore(
                        " §7➥ Aktion§7 ➞ " + balanceAction + balanceAction.getPrettyString(),
                        " §7➥ " + ItemColors.BANK.getPrimary() + "Kontostand§7 ➞ " + ItemColors.BANK.getAccent() + this.guild.getBalance(),
                        " §7➥ §aBenutzerdefinierte Mengen§7 ➞ Benutze dazu bitte §a/guild bank " + balanceAction.name().toLowerCase() + " §2[Wert]"
                )
                .build());
    }

}
