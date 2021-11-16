package de.nebelniek.inventory.guild.member;

import de.nebelniek.content.guild.response.GuildContentResponse;
import de.nebelniek.content.guild.response.GuildResponseState;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.inventory.template.TemplateInventoryBackgroundProvider;
import de.nebelniek.inventory.types.GuildInventory;
import de.notecho.ItemBuilder;
import de.notecho.inventory.click.ClickOption;
import de.notecho.inventory.click.OptionClickEvent;
import de.notecho.inventory.click.OptionExecutor;
import de.notecho.inventory.click.OptionHandler;
import org.bukkit.Material;

public class OneMemberInventory extends GuildInventory {

    private final ICloudUser cloudUser;

    public OneMemberInventory(ICloudUser cloudUser, ICloudUser opener, IGuild guild) {
        super(TemplateInventoryBackgroundProvider.threexnine("§8» " + cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + " §r§8«"), guild, opener);
        this.cloudUser = cloudUser;
        setup();
    }

    @Override
    public void setup() {
        //11,13,15
        if (cloudUser.getGuildRole().oneDown() != null)
            addClickOption(new ClickOption(11, ItemBuilder.item(Material.RED_CONCRETE_POWDER)
                    .setDisplayName("§8» §cDegradieren §r§8«")
                    .setLore(" §7➥ §aLinksklick§7 ➞ §cDegradiere §7" + cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + " §7zum " + cloudUser.getGuildRole().oneDown().getPrettyName())
                    .build()));
        else
            getInventory().setItem(11, ItemBuilder.item(Material.GRAY_CONCRETE_POWDER)
                    .setDisplayName("§8» §7Nicht Verfügbar §r§8«")
                    .setLore(" §7➥ Dieses Mitglied kann nicht weiter degradiert werden!")
                    .build());
        addClickOption(new ClickOption(15, ItemBuilder.item(Material.LIME_CONCRETE_POWDER)
                .setDisplayName("§8» §aBefördern §r§8«")
                .setLore(" §7➥ §aLinksklick§7 ➞ §aBefördere §7" + cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + " §7zum " + cloudUser.getGuildRole().oneUp().getPrettyName())
                .build()));
        addClickOption(new ClickOption(13, ItemBuilder.item(Material.LIME_CONCRETE_POWDER)
                .setDisplayName("§8» §cKicken §r§8«")
                .setLore(" §7➥ §aLinksklick§7 ➞ §cKicke §7" + cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + " §7aus der Gilde")
                .build()));
        addClickOption(new ClickOption(26, ItemBuilder.item(Material.GREEN_STAINED_GLASS_PANE)
                .setDisplayName("§8» §c§lZurück §r§8«")
                .build()));
        addOptionExecutor(new OptionExecutor(26, event -> new MemberOverviewInventory(opener, guild).open(event.getPlayer())));
    }

    @OptionHandler(11)
    public void onDemote(OptionClickEvent event) {
        GuildContentResponse response = guildContentService.degradeMember(cloudUser, opener);
        sendResponse(event.getPlayer(), response);
        if (response.state().equals(GuildResponseState.SUCCESS))
            event.getPlayer().closeInventory();
    }

    @OptionHandler(13)
    public void onPromote(OptionClickEvent event) {
        GuildContentResponse response = guildContentService.promoteMember(cloudUser, opener);
        sendResponse(event.getPlayer(), response);
        if (response.state().equals(GuildResponseState.SUCCESS))
            event.getPlayer().closeInventory();
    }

    @OptionHandler(15)
    public void onKick(OptionClickEvent event) {
        GuildContentResponse response = guildContentService.kickMember(cloudUser, opener);
        sendResponse(event.getPlayer(), response);
        if (response.state().equals(GuildResponseState.SUCCESS))
            event.getPlayer().closeInventory();
    }

}
