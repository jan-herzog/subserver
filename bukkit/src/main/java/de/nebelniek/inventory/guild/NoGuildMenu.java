package de.nebelniek.inventory.guild;

import de.nebelniek.configuration.Prices;
import de.nebelniek.inventory.template.TemplateInventoryBackgroundProvider;
import de.nebelniek.inventory.types.GuildInventory;
import de.nebelniek.inventory.util.ItemColors;
import de.nebelniek.inventory.util.MenuName;
import de.nebelniek.utils.Prefix;
import de.notecho.ItemBuilder;
import de.notecho.inventory.click.ClickOption;
import de.notecho.inventory.click.OptionClickEvent;
import de.notecho.inventory.click.OptionHandler;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;

public class NoGuildMenu extends GuildInventory {

    public NoGuildMenu() {
        super(TemplateInventoryBackgroundProvider.threexnine(MenuName.NOGUILD_MAIN_MENU.getName()));
    }

    @Override
    public void setup() {
        addClickOption(new ClickOption(13, ItemBuilder.item(Material.CARTOGRAPHY_TABLE)
                .setDisplayName("§8» " + ItemColors.GUILD.getPrimary() + "§lGilde erstellen §r§8«")
                .setLore(
                        "§7Erstelle deine" + ItemColors.LEAVE.getAccent() + " eigene Gilde§7 und " + ItemColors.LEAVE.getAccent() + "verbünde§7 dich mit anderen Spielern.",
                        " §7➥ §6Kosten§7 ➞ §e" + Prices.GUILD_CREATE.getPrice(),
                        " §7➥ §aLinksklick§7 ➞ Erstellen"
                )
                .build()));
    }

    @OptionHandler(13)
    public void onCreate(OptionClickEvent event) {
        new AnvilGUI.Builder()
                .onClose(player -> player.sendMessage(Prefix.GUILD + "§cDu hast die Gilden-Erstellung abgebrochen!"))
                .onComplete((player, text) -> {
                    if (text.contains(" "))
                        return AnvilGUI.Response.text("No spaces allowed!");
                    this.cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> this.guildContentService.createGuild(cloudUser, text));
                    return AnvilGUI.Response.close();
                })
                .itemLeft(ItemBuilder.item(Material.CARTOGRAPHY_TABLE).setDisplayName("§8» " + ItemColors.GUILD.getPrimary() + "§lWie soll deine Gilde heißen? §r§8«").build())
                .itemRight(ItemBuilder.item(Material.CARTOGRAPHY_TABLE).setDisplayName("").build())
                .title("§8» " + ItemColors.GUILD.getPrimary() + "§lWie soll deine Gilde heißen? §r§8«")
                .open(event.getPlayer());
    }
}
