package de.nebelniek.listener;

import de.nebelniek.components.combatlog.CombatLogService;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RegionProtectionListener implements Listener {

    private final GuildManagingService guildManagingService;

    private final CombatLogService combatLogService;

    private final CloudUserManagingService cloudUserManagingService;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && !event.getClickedBlock().getType().equals(Material.AIR))
            if (isForbidden(event.getPlayer(), event.getClickedBlock().getLocation()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onFloat(BlockFromToEvent event) {
        if (isForbidden(event.getToBlock().getLocation(), event.getToBlock().getLocation()))
            event.setCancelled(true);
    }

    private boolean isForbidden(Player player, Location location) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(player.getUniqueId());
        IGuild guild = guildManagingService.getGuildAt(location.getX(), location.getZ());
        if (guild != null)
            if (cloudUser.getGuild() != guild) {
                if (cloudUser.getGuild() == null || (cloudUser.getGuild() != null && !cloudUser.getGuild().getAllies().contains(guild))) {
                    if (player.hasPermission("subserver.bypass"))
                        return false;
                    player.sendMessage(Prefix.GUILD + "§cDies ist das Gebiet von " + guild.getColor() + guild.getName() + "§c, du darfst hier nicht interagieren!");
                    return true;
                }
            } else {
                if (combatLogService.isInFight(cloudUser)) {
                    player.sendMessage(Prefix.COMBAT + "§cDu bist im Kampf! §7Du darfst nur außerhalb deines Grundstückes interagieren.");
                    return true;
                }
            }
        return false;
    }

    private boolean isForbidden(Location from, Location to) {
        IGuild fromGuild = guildManagingService.getGuildAt(from.getX(), from.getZ());
        IGuild toGuild = guildManagingService.getGuildAt(to.getX(), to.getZ());
        return toGuild != null && fromGuild == null;
    }

}
