package de.nebelniek.inventory.guild.region;

import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.util.Direction;
import de.nebelniek.inventory.template.TemplateInventoryBackgroundProvider;
import de.nebelniek.inventory.types.GuildInventory;
import de.nebelniek.inventory.util.HeadTextureHash;
import de.nebelniek.inventory.util.ItemColors;
import de.notecho.ItemBuilder;
import de.notecho.inventory.click.ClickOption;
import de.notecho.inventory.click.OptionClickEvent;
import de.notecho.inventory.click.OptionHandler;
import org.bukkit.Material;

public class RegionExpandMenu extends GuildInventory {

    public RegionExpandMenu(IGuild guild) {
        super(TemplateInventoryBackgroundProvider.fivexnine(""), guild);
    }

    @Override
    public void setup() {
        //4,20,24,40
        getInventory().setItem(22, ItemBuilder.item(Material.FILLED_MAP)
                .setDisplayName("§8» " + ItemColors.REGION.getPrimary() + "§lRegion §r§8«")
                .setLore(
                        "§7Beanspruche eine " + ItemColors.REGION.getAccent() + "Region §7und nur " + ItemColors.REGION.getAccent() + "du und deine Gilde §7können darauf bauen!",
                        " §7➥ §aAktuelle Region§7 ➞ §e" + ((int) guild.getRegion().getAX()) + "§7, §e" + ((int) guild.getRegion().getAZ()) + "§7 - §e" + "§e" + ((int) guild.getRegion().getBX()) + "§7, §e" + ((int) guild.getRegion().getBZ())
                )
                .build());
        addClickOption(new ClickOption(4, ItemBuilder.item(Material.PLAYER_HEAD)
                .setDisplayName("§8» " + ItemColors.REGION.getPrimary() + "§lNorden §r§8«")
                .setLore(" §7➥ §aErweitere§7 deine Region nach " + ItemColors.REGION.getAccent() + "Norden")
                .setSkullHash(HeadTextureHash.ARROW_UP.getHash())
                .build()));
        addClickOption(new ClickOption(4, ItemBuilder.item(Material.PLAYER_HEAD)
                .setDisplayName("§8» " + ItemColors.REGION.getPrimary() + "§lOsten §r§8«")
                .setLore(" §7➥ §aErweitere§7 deine Region nach " + ItemColors.REGION.getAccent() + "Osten")
                .setSkullHash(HeadTextureHash.ARROW_RIGHT.getHash())
                .build()));
        addClickOption(new ClickOption(4, ItemBuilder.item(Material.PLAYER_HEAD)
                .setDisplayName("§8» " + ItemColors.REGION.getPrimary() + "§lSüden §r§8«")
                .setLore(" §7➥ §aErweitere§7 deine Region nach " + ItemColors.REGION.getAccent() + "Süden")
                .setSkullHash(HeadTextureHash.ARROW_DOWN.getHash())
                .build()));
        addClickOption(new ClickOption(4, ItemBuilder.item(Material.PLAYER_HEAD)
                .setDisplayName("§8» " + ItemColors.REGION.getPrimary() + "§lWesten §r§8«")
                .setLore(" §7➥ §aErweitere§7 deine Region nach " + ItemColors.REGION.getAccent() + "Westen")
                .setSkullHash(HeadTextureHash.ARROW_DOWN.getHash())
                .build()));
    }

    @OptionHandler(4)
    public void onNorth(OptionClickEvent event) {
        cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> sendResponse(event.getPlayer(), guildContentService.expandRegion(cloudUser, Direction.NORTH)));
    }

    @OptionHandler(20)
    public void onEast(OptionClickEvent event) {
        cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> sendResponse(event.getPlayer(), guildContentService.expandRegion(cloudUser, Direction.EAST)));
    }
    @OptionHandler(24)
    public void onSouth(OptionClickEvent event) {
        cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> sendResponse(event.getPlayer(), guildContentService.expandRegion(cloudUser, Direction.SOUTH)));
    }
    @OptionHandler(40)
    public void onWest(OptionClickEvent event) {
        cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> sendResponse(event.getPlayer(), guildContentService.expandRegion(cloudUser, Direction.WEST)));
    }


}
