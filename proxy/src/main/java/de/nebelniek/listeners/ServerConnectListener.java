package de.nebelniek.listeners;

import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.services.maintenance.MaintenanceKey;
import de.nebelniek.services.maintenance.MaintenanceService;
import de.nebelniek.utils.ClickCooldown;
import de.nebelniek.utils.HexColors;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ServerConnectListener implements Listener {

    private final HexColors hexColors;

    private final CloudUserManagingService cloudUserManagingService;

    private final MaintenanceService maintenanceService;

    @EventHandler
    public void onServerSwitch(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo serverInfo = event.getTarget();
        for (MaintenanceKey value : MaintenanceKey.values())
            if (serverInfo.getName().contains(value.getGroup()))
                if (maintenanceService.get(value)) {
                    if (!player.hasPermission("proxy.maintenance.bypass"))
                        event.setCancelled(true);
                    return;
                }
        if (serverInfo.getName().contains("Subserver")) {
            if (!ClickCooldown.isAbleToClick(player.getUniqueId())) {
                player.sendMessage(Prefix.PROXY + "Bitte warte noch " + ClickCooldown.getCooldown(player.getUniqueId()) + " Sekunden...");
                event.setCancelled(true);
                return;
            }
            ICloudUser cloudUser = cloudUserManagingService.loadUserSync(player.getUniqueId());
            ClickCooldown.registerClick(event.getPlayer().getUniqueId());
            if (!cloudUser.isSubbed()) {
                player.sendMessage(Prefix.PROXY + "Du bist kein §5Twitch§7-Subscriber, weshalb du diesen Server §cnicht§7 betreten darfst.");
                event.setCancelled(true);
                return;
            }
        }
        player.setTabHeader(
                new ComponentBuilder()
                        .append("§e§lNebelniek Subserver")
                        .append("\n")
                        .create(),
                new ComponentBuilder()
                        .append("\n")
                        .append("§7Aktueller Server\n")
                        .append("§8" + serverInfo.getName() + "\n")
                        .append("\n")
                        .append("§5twitch.tv/nebelniek")
                        .create());
    }

}
