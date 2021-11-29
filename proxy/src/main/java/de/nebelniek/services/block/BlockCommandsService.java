package de.nebelniek.services.block;

import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.springframework.stereotype.Service;

@Service
public class BlockCommandsService implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        String message = event.getMessage();

    }


}
