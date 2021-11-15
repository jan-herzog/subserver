package de.nebelniek.inventory.guild;

import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.inventory.template.TemplateInventoryBackgroundProvider;
import de.nebelniek.inventory.types.GuildInventory;
import de.nebelniek.inventory.util.ItemColors;
import de.nebelniek.inventory.util.MenuName;
import de.notecho.ItemBuilder;
import de.notecho.inventory.click.ClickOption;
import de.notecho.inventory.click.OptionClickEvent;
import de.notecho.inventory.click.OptionHandler;

import org.bukkit.Material;

public class GuildMainMenu extends GuildInventory {

    private final IGuild guild;

    public GuildMainMenu(IGuild guild) {
        super(TemplateInventoryBackgroundProvider.fivexnine(MenuName.GUILD_MAIN_MENU.getName()));
        this.guild = guild;
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
                        (guild.getRegion() != null ? " §7➥ §aLinksklick§7 ➞ Region §aerweitern" : " §7➥ §aLinksklick§7 ➞ Region §aclaimen")
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
                        " §7➥ §aLinksklick§7 ➞ Teleportieren"
                )
                .build()));
    }

    @OptionHandler(4)
    public void onBank(OptionClickEvent event) {
        //TODO: Open BankMenu
    }

    @OptionHandler(19)
    public void onRegion(OptionClickEvent event) {
        //TODO: Open RegionMenu
    }

    @OptionHandler(40)
    public void onMember(OptionClickEvent event) {
        //TODO: Open MemberMenu
    }

    @OptionHandler(25)
    public void onLeave(OptionClickEvent event) {
        cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(guildContentService::leaveGuild);
    }

    @OptionHandler(22)
    public void onHome(OptionClickEvent event) {

    }


}
