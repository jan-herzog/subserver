package de.nebelniek.inventory.guild.member;

import de.nebelniek.Subserver;
import de.nebelniek.content.guild.GuildContentService;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.util.GuildRole;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.inventory.template.TemplateInventoryBackgroundProvider;
import de.nebelniek.inventory.util.ItemColors;
import de.nebelniek.utils.Prefix;
import de.notecho.ItemBuilder;
import de.notecho.inventory.click.OptionExecutor;
import de.notecho.inventory.inventories.SiteInventory;
import de.notecho.inventory.site.SiteItem;
import de.notecho.inventory.site.SiteItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class MemberOverviewInventory extends SiteInventory {

    private final GuildContentService guildContentService;
    private final CloudUserManagingService cloudUserManagingService;
    private final IGuild guild;
    private final ICloudUser opener;

    public MemberOverviewInventory(ICloudUser opener, IGuild guild) {
        super(TemplateInventoryBackgroundProvider.fivexnine("§8» " + ItemColors.MEMBER.getPrimary() + "§lMitlgieder §r§8«"));
        this.guildContentService = Subserver.getContext().getBean(GuildContentService.class);
        this.cloudUserManagingService = Subserver.getContext().getBean(CloudUserManagingService.class);
        this.guild = guild;
        this.opener = opener;
        setup();
    }

    @Override
    public void setup() {
        setItemsPerSite(0, 4 * 9 - 1);
        setFillItem(ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).setDisplayName("").build());
        setSiteItem(SiteItemType.PREVIOUSSITE, new SiteItem(ItemBuilder.item(Material.ARROW).setDisplayName("§8« §7Seite " + ItemColors.MEMBER.getAccent() + "{previousSite}").build(), 38));
        setSiteItem(SiteItemType.NEXTSITE, new SiteItem(ItemBuilder.item(Material.ARROW).setDisplayName("§7Seite " + ItemColors.MEMBER.getAccent() + "{nextSite} §8»").build(), 42));
        setSiteItem(SiteItemType.CURRENTSITE, new SiteItem(ItemBuilder.item(Material.PLAYER_HEAD).setDisplayName("§8» §7Seite " + ItemColors.MEMBER.getPrimary() + "{site} §r§8«").build(), 40));
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (ICloudUser cloudUser : guild.getMember())
            itemStacks.add(ItemBuilder.item(Material.PLAYER_HEAD)
                    .setSkullHash(cloudUser.getTextureHash())
                    .setDisplayName(cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName())
                    .setLore(" §7➥ " + ItemColors.MEMBER.getAccent() + "Rang§7 ➞ " + cloudUser.getGuildRole().getPrettyName(), opener.getGuildRole().isHigherOrEquals(guild.getSettings().getManageMembersRole()) ? " §7➥ §aLinksklick§7 ➞ Weitere Optionen" : "")
                    .build());
        setContent(itemStacks);
    }

    @Override
    public OptionExecutor contentOptionExecutor() {
        return new OptionExecutor(-1, event -> {
            String name = ChatColor.stripColor(event.getItemStack().getDisplayName());
            if (!opener.getGuildRole().isHigherOrEquals(guild.getSettings().getManageMembersRole()))
                return;
            ICloudUser cloudUser = guild.getMember().stream().filter(c -> c.getLastUserName().equals(name)).findAny().orElse(null);
            if (cloudUser == null)
                return;
            if (cloudUser.getGuildRole().isHigherOrEquals(GuildRole.LEADER)) {
                event.getPlayer().sendMessage(Prefix.GUILD + "§cDieses Mitglied ist " + GuildRole.LEADER.getPrettyName() + "§c und kann somit nicht bearbeitet werden!");
                return;
            }
            new OneMemberInventory(cloudUser, opener, guild).open(event.getPlayer());
        });
    }
}
