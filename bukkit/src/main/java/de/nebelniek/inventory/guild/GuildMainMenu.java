package de.nebelniek.inventory.guild;

import de.nebelniek.content.guild.BalanceAction;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.util.HomePoint;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.inventory.guild.bank.BankTransferMenu;
import de.nebelniek.inventory.guild.member.MemberOverviewInventory;
import de.nebelniek.inventory.guild.region.RegionExpandMenu;
import de.nebelniek.inventory.template.TemplateInventoryBackgroundProvider;
import de.nebelniek.inventory.types.GuildInventory;
import de.nebelniek.inventory.util.ItemColors;
import de.nebelniek.inventory.util.MenuName;
import de.notecho.ItemBuilder;
import de.notecho.inventory.click.ClickOption;
import de.notecho.inventory.click.OptionClickEvent;
import de.notecho.inventory.click.OptionHandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class GuildMainMenu extends GuildInventory {


    public GuildMainMenu(IGuild guild, ICloudUser opener) {
        super(TemplateInventoryBackgroundProvider.fivexnine(MenuName.GUILD_MAIN_MENU.getName()), guild, opener);
        setup();
    }

    @Override
    public void setup() {
        addClickOption(new ClickOption(4, ItemBuilder.item(Material.RAW_GOLD_BLOCK)
                .setDisplayName("§8» " + ItemColors.BANK.getPrimary() + "§lBank §r§8«")
                .setLore(
                        "§7Rufe dein " + ItemColors.BANK.getAccent() + "Guthaben §7ab, was in der §" + ItemColors.BANK.getAccent() + "Gilden-Bank§7 gespeichert ist.",
                        " §7➥ §aLinksklick§7 ➞ Geld §alagern",
                        " §7➥ §cRechtsklick§7 ➞ Geld §cabheben"
                )
                .build()));
        addClickOption(new ClickOption(19, ItemBuilder.item(Material.FILLED_MAP)
                .setDisplayName("§8» " + ItemColors.REGION.getPrimary() + "§lRegion §r§8«")
                .setLore(
                        "§7Beanspruche eine " + ItemColors.REGION.getAccent() + "Region §7und nur " + ItemColors.REGION.getAccent() + "du und deine Gilde §7können darauf bauen!",
                        (guild.getRegion() != null ? " §7➥ §aLinksklick§7 ➞ Region §aerweitern" : " §7➥ §aLinksklick§7 ➞ Region §aclaimen"),
                        (guild.getRegion() != null ? " §7➥ §aRechtsklick§7 ➞ Region §clöschen" : "")
                )
                .build()));
        addClickOption(new ClickOption(40, ItemBuilder.item(Material.PLAYER_HEAD)
                .setSkullHash(guild.getOwner().getTextureHash())
                .setDisplayName("§8» " + ItemColors.MEMBER.getPrimary() + "§lMitglieder §r§8«")
                .setLore(
                        ItemColors.MEMBER.getAccent() + "Verwalte§7 die " + ItemColors.MEMBER.getAccent() + "Mitglieder §7deiner Gilde.",
                        " §7➥ §aLinksklick§7 ➞ Öffne das Mitglieder Menu"
                )
                .build()));
        addClickOption(new ClickOption(25, ItemBuilder.item(Material.RED_BANNER)
                .setDisplayName("§8» " + ItemColors.LEAVE.getPrimary() + "§lGilde verlassen §r§8«")
                .setLore(
                        "§7Sag Tschüss zu deinen Kollegen und " + ItemColors.LEAVE.getAccent() + "verlasse§7 deine Gilde.",
                        " §7➥ §aLinksklick§7 ➞ Verlassen"
                )
                .build()));
        addClickOption(new ClickOption(22, ItemBuilder.item(Material.ENDER_PEARL)
                .setDisplayName("§8» " + ItemColors.HOME.getPrimary() + "§lHome §r§8«")
                .setLore(
                        ItemColors.HOME.getAccent() + "Teleportiere§7 dich " + ItemColors.HOME.getAccent() + "nach Hause§7.",
                        " §7➥ " + ItemColors.HOME.getAccent() + "Dein Home§7 ➞ " + (guild.getHome() == null ? "§cNicht gesetzt!" : guild.getHome().string()),
                        " §7➥ §aLinksklick§7 ➞ Teleportieren"
                )
                .build()));
    }

    @OptionHandler(4)
    public void onBank(OptionClickEvent event) {
        new BankTransferMenu(guild, event.getInventoryClickEvent().isLeftClick() ? BalanceAction.DEPOSIT : BalanceAction.WITHDRAW, opener).open(event.getPlayer());
    }

    @OptionHandler(19)
    public void onRegion(OptionClickEvent event) {
        if (guild.getRegion() == null) {
            sendResponse(event.getPlayer(), guildContentService.claimRegion(opener, event.getPlayer().getLocation()));
            event.getPlayer().closeInventory();
            return;
        }
        if (event.getInventoryClickEvent().isLeftClick())
            new RegionExpandMenu(guild, opener).open(event.getPlayer());
        else {
            sendResponse(event.getPlayer(), guildContentService.disposeRegion(opener));
            event.getPlayer().closeInventory();
        }
    }

    @OptionHandler(40)
    public void onMember(OptionClickEvent event) {
        new MemberOverviewInventory(opener, guild).open(event.getPlayer());
    }

    @OptionHandler(25)
    public void onLeave(OptionClickEvent event) {
        sendResponse(event.getPlayer(), guildContentService.leaveGuild(opener));
        event.getPlayer().closeInventory();
    }

    @OptionHandler(22)
    public void onHome(OptionClickEvent event) {
        sendResponse(event.getPlayer(), guildContentService.tpHome(opener, event.getPlayer()));
    }


}
