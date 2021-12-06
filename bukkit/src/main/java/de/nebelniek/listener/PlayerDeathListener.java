package de.nebelniek.listener;

import de.nebelniek.utils.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.springframework.stereotype.Component;

@Component
public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(Prefix.SUBSERVER + event.getDeathMessage());
    }

}
