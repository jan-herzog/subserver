package de.nebelniek.listeners;

import de.nebelniek.ProxyConfiguration;
import de.nebelniek.utils.HexColors;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProxyPingListener implements Listener {

    private final HexColors hexColors;

    @EventHandler
    public void onPing(ProxyPingEvent event) {
        ServerPing serverPing = event.getResponse();
        serverPing.setDescription(
                hexColors.main() + "§l          Nebelniek Subserver §7[" + hexColors.accent() + "1§7." + hexColors.accent() + "17§7." + hexColors.accent() + "1§7]§r\n" +
                        "                   §5twitch.tv/nebelniek"
        );
        event.setResponse(serverPing);
    }


}
