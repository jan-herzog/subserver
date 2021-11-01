package de.nebelniek.listeners;

import de.nebelniek.database.user.CloudUserRepository;
import de.nebelniek.services.hashcode.HashcodeService;
import de.nebelniek.services.twitch.TwitchSubscriptionService;
import de.nebelniek.services.verify.VerifyService;
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

    private final VerifyService verifyService;
    private final TwitchSubscriptionService twitchSubscriptionService;
    private final CloudUserRepository cloudUserRepository;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        cloudUserRepository.findByUuidAsync(player.getUniqueId()).thenAccept(cloudUser -> {
            if (!cloudUser.isSubbed())
                verifyService.showVerifySuggestion(player);
        });
    }

}
