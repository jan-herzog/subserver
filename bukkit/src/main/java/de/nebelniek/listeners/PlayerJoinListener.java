package de.nebelniek.listeners;

import de.nebelniek.hashcode.HashcodeService;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PlayerJoinListener implements Listener {

    private final HashcodeService hashcodeService;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        hashcodeService.storeHash(player.getUniqueId());
        player.sendMessage("https://verify.nebelniek.de/auth?hash=" + hashcodeService.getHash(player.getUniqueId()));
        System.out.println("https://verify.nebelniek.de/auth?hash=" + hashcodeService.getHash(player.getUniqueId()));
    }

}
