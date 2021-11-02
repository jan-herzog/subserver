package de.nebelniek.listeners;

import de.nebelniek.database.user.CloudUser;
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
        cloudUserRepository.findByUuidEquals(player.getUniqueId()).thenAccept(cloudUser -> {
            if(cloudUser == null) {
                CloudUser newCloudUser = CloudUser.builder().uuid(player.getUniqueId()).lastUserName(player.getName()).subbed(false).build();
                cloudUserRepository.save(newCloudUser);
                verifyService.showVerifySuggestion(player);
                return;
            }
            if (cloudUser.getTwitchId() == null)
                verifyService.showVerifySuggestion(player);
        });
    }

}
