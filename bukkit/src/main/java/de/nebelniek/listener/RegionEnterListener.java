package de.nebelniek.listener;

import de.nebelniek.database.guild.Region;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.interfaces.IRegion;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RegionEnterListener implements Listener {

    private final Map<ICloudUser, IRegion> lastRegions = new HashMap<>();

    private final CloudUserManagingService cloudUserManagingService;

    private final GuildManagingService guildManagingService;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (lastRegions.entrySet().stream().anyMatch(entry -> entry.getKey().getUuid().equals(player.getUniqueId()))) {
            ICloudUser cloudUser = lastRegions.entrySet().stream().filter(entry -> entry.getKey().getUuid().equals(player.getUniqueId())).findAny().get().getKey();
            IGuild guild = guildManagingService.getGuild(player.getLocation().getX(), player.getLocation().getZ());
            if (!lastRegions.get(cloudUser).equals(guild.getRegion())) {
                lastRegions.replace(cloudUser, guild.getRegion());
                displayTitle(guild, player);
                return;
            }
        }
        cloudUserManagingService.loadUser(player.getUniqueId()).thenAccept(cloudUser -> {
            IGuild guild = guildManagingService.getGuild(player.getLocation().getX(), player.getLocation().getZ());
            lastRegions.put(cloudUser, guild.getRegion());
            displayTitle(guild, player);
        });
    }

    private void displayTitle(IGuild guild, Player player) {
        player.sendTitle("§aDu betrittst:", guild.getColor() + guild.getName(), 10, 30, 10);
    }
}
