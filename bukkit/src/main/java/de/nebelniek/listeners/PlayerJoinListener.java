package de.nebelniek.listeners;

import de.nebelniek.database.user.CloudUser;
import de.nebelniek.database.user.CloudUserManagingService;
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
    private final CloudUserManagingService cloudUserRepository;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(player.getName() + " joined!");
        cloudUserRepository.createUserIfNotExists(player.getUniqueId(), player.getName()).thenAccept(cloudUser -> {
            if (cloudUser.getTwitchId() == null)
                verifyService.showVerifySuggestion(player);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

}
