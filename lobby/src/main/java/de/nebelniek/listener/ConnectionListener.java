package de.nebelniek.listener;

import de.nebelniek.configuration.LobbyConfiguration;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import lombok.RequiredArgsConstructor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ConnectionListener implements Listener {

    private final LobbyConfiguration configuration;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.teleport(configuration.getSpawnLocation());
        event.setJoinMessage(null);
        AnimatedBallEffect effect = new AnimatedBallEffect(configuration.getEffectManager());
        effect.setLocation(player.getLocation());
        effect.setTargetLocation(player.getLocation().add(0, 2, 0));
        effect.start();
        player.getInventory().clear();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        event.getPlayer().getWorld().playEffect(event.getPlayer().getLocation().add(0, .5, 0), Effect.SMOKE, 1);
    }

}
