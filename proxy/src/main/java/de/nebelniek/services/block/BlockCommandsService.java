package de.nebelniek.services.block;

import de.nebelniek.utils.Prefix;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.springframework.stereotype.Service;

@Service
public class BlockCommandsService implements Listener {

    private static final String[] array = {
            "pl",
            "plugins",
            "?",
            "help",
            "version",
            "ver",
            "purpur",
            "bungee",
            "cloud",
    };

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) {

            return;
        }

        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();
        if (!message.startsWith("/"))
            return;
        if (sender.hasPermission("admin.bypasscommandcheck"))
            return;

        String firstArg;
        if (message.contains(" "))
            firstArg = message.split(" ")[0].toLowerCase().substring(1);
        else
            firstArg = message.toLowerCase().substring(1);

        if (firstArg.contains(":")) {
            sender.sendMessage(Prefix.PROXY + "§cAus Sicherheitsgründen kannst du diesen Befehl nicht aufrufen.");
            event.setCancelled(true);
            return;
        }

        for (String s : array) {
            if (firstArg.toLowerCase().contains(s)) {
                sender.sendMessage(Prefix.PROXY + "§cDu darfst diesen Command nicht benutzen.");
                event.setCancelled(true);
                return;
            }
        }

    }


}
