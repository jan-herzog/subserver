package de.nebelniek.inventory.guild;

import de.nebelniek.Subserver;
import de.nebelniek.configuration.BukkitConfiguration;
import de.nebelniek.configuration.Prices;
import de.nebelniek.content.guild.response.GuildContentResponse;
import de.nebelniek.database.user.interfaces.ICloudUser;
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
import org.bukkit.plugin.java.JavaPlugin;

public class NoGuildMenu extends GuildInventory {

    private JavaPlugin plugin;

    public NoGuildMenu() {
        super(TemplateInventoryBackgroundProvider.threexnine(MenuName.NOGUILD_MAIN_MENU.getName()));
        this.plugin = Subserver.getContext().getBean(BukkitConfiguration.class).getPlugin();
        setup();
    }

    @Override
    public void setup() {
        addClickOption(new ClickOption(13, ItemBuilder.item(Material.CARTOGRAPHY_TABLE)
                .setDisplayName("§8» " + ItemColors.GUILD.getPrimary() + "§lGilde erstellen §r§8«")
                .setLore(
                        "§7Erstelle deine" + ItemColors.GUILD.getAccent() + " eigene Gilde§7 und " + ItemColors.GUILD.getAccent() + "verbünde§7 dich mit anderen Spielern.",
                        " §7➥ §6Kosten§7 ➞ §e" + Prices.GUILD_CREATE.getPrice(),
                        " §7➥ §aLinksklick§7 ➞ Erstellen"
                )
                .build()));
    }

    @OptionHandler(13)
    public void onCreate(OptionClickEvent event) {
        new AnvilGUI.Builder()
                .onClose(player -> {
                    ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(player.getUniqueId());
                    if (cloudUser.getGuild() == null)
                        player.sendMessage(Prefix.GUILD + "§cDu hast die Gilden-Erstellung abgebrochen!");
                })
                .onComplete((player, text) -> {
                    if (text.contains(" "))
                        return AnvilGUI.Response.text("No spaces allowed!");
                    this.cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> sendResponse(player, this.guildContentService.createGuild(cloudUser, text))).exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    });
                    return AnvilGUI.Response.close();
                })
                .plugin(plugin)
                .itemLeft(ItemBuilder.item(Material.CARTOGRAPHY_TABLE).setDisplayName(ItemColors.GUILD.getPrimary() + "§lWie soll deine Gilde heißen?").build())
                .title("§8» " + ItemColors.GUILD.getPrimary() + "§lGildenname §r§8«")
                .open(event.getPlayer());
    }
}
