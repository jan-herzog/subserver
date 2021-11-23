package de.nebelniek.inventory.guild.region;

import de.nebelniek.configuration.Prices;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.util.Direction;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.inventory.guild.GuildMainMenu;
import de.nebelniek.inventory.template.TemplateInventoryBackgroundProvider;
import de.nebelniek.inventory.types.GuildInventory;
import de.nebelniek.inventory.util.HeadTextureHash;
import de.nebelniek.inventory.util.ItemColors;
import de.notecho.ItemBuilder;
import de.notecho.inventory.click.ClickOption;
import de.notecho.inventory.click.OptionClickEvent;
import de.notecho.inventory.click.OptionExecutor;
import de.notecho.inventory.click.OptionHandler;
import org.bukkit.Material;

public class RegionExpandMenu extends GuildInventory {

    public RegionExpandMenu(IGuild guild, ICloudUser opener) {
        super(TemplateInventoryBackgroundProvider.fivexnine("§8» " + ItemColors.REGION.getPrimary() + "§lRegion §r§8«"), guild, opener);
        setup();
    }

    @Override
    public void setup() {
        //4,20,24,40
        update();
        addClickOption(new ClickOption(4, ItemBuilder.item(Material.PLAYER_HEAD)
                .setDisplayName("§8» " + ItemColors.REGION.getPrimary() + "§lNorden §r§8«")
                .setLore(
                        " §7➥ §6Kosten§7 ➞ §e" + guildContentService.getPrice(guild.getRegion(), Direction.NORTH),
                        " §7➥ §aErweitere§7 deine Region nach " + ItemColors.REGION.getAccent() + "Norden"
                )
                .setSkullHash(HeadTextureHash.ARROW_UP.getHash())
                .build()));
        addClickOption(new ClickOption(24, ItemBuilder.item(Material.PLAYER_HEAD)
                .setDisplayName("§8» " + ItemColors.REGION.getPrimary() + "§lOsten §r§8«")
                .setLore(
                        " §7➥ §6Kosten§7 ➞ §e" + guildContentService.getPrice(guild.getRegion(), Direction.EAST),
                        " §7➥ §aErweitere§7 deine Region nach " + ItemColors.REGION.getAccent() + "Osten"
                )
                .setSkullHash(HeadTextureHash.ARROW_RIGHT.getHash())
                .build()));
        addClickOption(new ClickOption(40, ItemBuilder.item(Material.PLAYER_HEAD)
                .setDisplayName("§8» " + ItemColors.REGION.getPrimary() + "§lSüden §r§8«")
                .setLore(
                        " §7➥ §6Kosten§7 ➞ §e" + guildContentService.getPrice(guild.getRegion(), Direction.SOUTH),
                        " §7➥ §aErweitere§7 deine Region nach " + ItemColors.REGION.getAccent() + "Süden"
                )
                .setSkullHash(HeadTextureHash.ARROW_DOWN.getHash())
                .build()));
        addClickOption(new ClickOption(20, ItemBuilder.item(Material.PLAYER_HEAD)
                .setDisplayName("§8» " + ItemColors.REGION.getPrimary() + "§lWesten §r§8«")
                .setLore(
                        " §7➥ §6Kosten§7 ➞ §e" + guildContentService.getPrice(guild.getRegion(), Direction.WEST),
                        " §7➥ §aErweitere§7 deine Region nach " + ItemColors.REGION.getAccent() + "Westen"
                )
                .setSkullHash(HeadTextureHash.ARROW_LEFT.getHash())
                .build()));
        addClickOption(new ClickOption(44, ItemBuilder.item(Material.GREEN_STAINED_GLASS_PANE)
                .setDisplayName("§8» §c§lZurück §r§8«")
                .build()));
        addOptionExecutor(new OptionExecutor(44, event -> new GuildMainMenu(guild, opener).open(event.getPlayer())));
    }

    @OptionHandler(4)
    public void onNorth(OptionClickEvent event) {
        sendResponse(event.getPlayer(), guildContentService.expandRegion(opener, Direction.NORTH));
        update();
    }

    @OptionHandler(20)
    public void onWest(OptionClickEvent event) {
        sendResponse(event.getPlayer(), guildContentService.expandRegion(opener, Direction.WEST));
        new RegionExpandMenu(guild, opener).open(event.getPlayer());
    }

    @OptionHandler(24)
    public void onEast(OptionClickEvent event) {
        sendResponse(event.getPlayer(), guildContentService.expandRegion(opener, Direction.EAST));
        new RegionExpandMenu(guild, opener).open(event.getPlayer());
    }

    @OptionHandler(40)
    public void onSouth(OptionClickEvent event) {
        sendResponse(event.getPlayer(), guildContentService.expandRegion(opener, Direction.SOUTH));
        new RegionExpandMenu(guild, opener).open(event.getPlayer());
    }

    private void update() {
        getInventory().setItem(22, ItemBuilder.item(Material.FILLED_MAP)
                .setDisplayName("§8» " + ItemColors.REGION.getPrimary() + "§lRegion §r§8«")
                .setLore(
                        "§7Beanspruche eine " + ItemColors.REGION.getAccent() + "Region §7und nur " + ItemColors.REGION.getAccent() + "du und deine Gilde §7können darauf bauen!",
                        " §7➥ §aAktuelle Region§7 ➞ §e" + ((int) guild.getRegion().getAX()) + "§7, §e" + ((int) guild.getRegion().getAZ()) + "§7 - §e" + "§e" + ((int) guild.getRegion().getBX()) + "§7, §e" + ((int) guild.getRegion().getBZ()),
                        " §7➥ §6Kosten§7 ➞ 1 Block = §e" + Prices.GUILD_EXPAND_REGION_BLOCK.getPrice()
                )
                .build());
    }


}
