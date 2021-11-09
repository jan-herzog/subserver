package de.nebelniek.listener;

import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RegionProtectionListener implements Listener {

    private final GuildManagingService guildManagingService;

    private final CloudUserManagingService cloudUserManagingService;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (isForbidden(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        if (isForbidden(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(BlockBreakEvent event) {
        if (isForbidden(event.getPlayer()))
            event.setCancelled(true);
    }


    private boolean isForbidden(Player player) {
        IGuild guild = guildManagingService.getGuild(player.getLocation().getX(), player.getLocation().getZ());
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(player.getUniqueId());
        if (cloudUser.getGuild() != guild && (cloudUser.getGuild() != null && !cloudUser.getGuild().getAllies().contains(guild))) {
            player.sendMessage(Prefix.GUILD + "§cDies ist das Gebiet von " + guild.getColor() + guild.getName() + "§c, du darfst hier nicht interagieren!");
            return true;
        }
        return false;
    }

}
