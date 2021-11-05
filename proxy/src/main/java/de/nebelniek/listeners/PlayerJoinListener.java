package de.nebelniek.listeners;

import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.services.twitch.TwitchSubscriptionService;
import de.nebelniek.services.verify.VerifyService;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PlayerJoinListener implements Listener {

    private final VerifyService verifyService;
    private final TwitchSubscriptionService twitchSubscriptionService;
    private final CloudUserManagingService cloudUserRepository;

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        cloudUserRepository.createUserIfNotExists(player.getUniqueId(), player.getName()).thenAccept(cloudUser -> {
            if (cloudUser.getTwitchId() == null)
                verifyService.showVerifySuggestion(player);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

}
