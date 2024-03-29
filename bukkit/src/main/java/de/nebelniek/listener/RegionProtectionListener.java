package de.nebelniek.listener;

import com.destroystokyo.paper.event.entity.CreeperIgniteEvent;
import de.nebelniek.components.combatlog.CombatLogService;
import de.nebelniek.components.spawnprotection.SpawnProtectionService;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RegionProtectionListener implements Listener {

    private final GuildManagingService guildManagingService;

    private final CombatLogService combatLogService;

    private final CloudUserManagingService cloudUserManagingService;

    private final SpawnProtectionService spawnProtectionService;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && !event.getClickedBlock().getType().equals(Material.AIR))
            if (isForbidden(event.getPlayer(), event.getClickedBlock().getLocation()))
                event.setCancelled(true);
        if (event.getItem() != null && (event.getItem().getType().equals(Material.SPLASH_POTION) || event.getItem().getType().equals(Material.LINGERING_POTION)))
            if (isForbidden(event.getPlayer(), event.getClickedBlock().getLocation()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (!(event.getDamager() instanceof Arrow arrow))
            return;
        if (!(arrow.getShooter() instanceof Player player))
            return;
        if (isForbidden(player, arrow.getLocation()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onCreeper(EntityExplodeEvent event) {
        Location location = event.getEntity().getLocation();
        IGuild guild = guildManagingService.getGuildAt(location.getWorld().getName(), location.getX(), location.getZ());
        if (guild != null)
            event.setCancelled(true);
    }


    @EventHandler
    public void onPlaceSpawn(PlayerBucketEmptyEvent event) {
        if (spawnProtectionService.isInSpawn(event.getBlock().getLocation())) {
            event.getPlayer().sendMessage(Prefix.SUBSERVER + "Du darfst am Spawn §ckeine§7 Flüssigkeiten platzieren.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFloat(BlockFromToEvent event) {
        if (isForbidden(event.getBlock().getLocation(), event.getToBlock().getLocation()))
            event.setCancelled(true);
    }

    private boolean isForbidden(Player player, Location location) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(player.getUniqueId());
        IGuild guild = guildManagingService.getGuildAt(location.getWorld().getName(), location.getX(), location.getZ());
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
        IGuild fromGuild = guildManagingService.getGuildAt(from.getWorld().getName(), from.getX(), from.getZ());
        IGuild toGuild = guildManagingService.getGuildAt(to.getWorld().getName(), to.getX(), to.getZ());
        return toGuild != null && fromGuild == null;
    }

}
