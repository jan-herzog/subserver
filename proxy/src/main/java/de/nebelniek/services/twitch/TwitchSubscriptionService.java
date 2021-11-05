package de.nebelniek.services.twitch;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.Subscription;
import de.nebelniek.database.user.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.Prefix;
import de.nebelniek.utils.TwitchTokens;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TwitchSubscriptionService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final TwitchClient twitchClient;
    private final CloudUserManagingService cloudUserRepository;

    public CompletableFuture<Boolean> isSubbed(UUID uuid) {
        return this.isSubbed(cloudUserRepository.loadUserSync(uuid));
    }

    public CompletableFuture<Boolean> isSubbed(ICloudUser cloudUser) {
        return CompletableFuture.supplyAsync(() -> {
            if (cloudUser.getTwitchId() == null)
                return false;
            if (cloudUser.getTwitchId().equalsIgnoreCase(""))
                return false;
            List<Subscription> subscriptions = twitchClient.getHelix().getSubscriptionsByUser(TwitchTokens.NEBELNIEK.getToken(), TwitchTokens.NEBELNIEK.getChannelId(), Collections.singletonList(cloudUser.getTwitchId())).execute().getSubscriptions();
            if (subscriptions.size() == 0)
                return false;
            cloudUser.setSubbed(subscriptions.get(0) != null);
            cloudUser.saveAsync();
            return subscriptions.get(0) != null;
        });
    }

    public void notifyPlayer(ProxiedPlayer player) {
        if (player == null) {
            LOGGER.debug("Notify cancelled because player was not online!");
            return;
        }
        this.isSubbed(player.getUniqueId()).thenAccept(subbed -> {
            if (subbed)
                player.sendMessage(Prefix.TWITCH + "Du bist §5Twitch-Abonnent§7 und kannst somit dem Server beitreten!");
            else
                player.sendMessage(Prefix.TWITCH + "Du bist §ckein §5Twitch-Abonnent§7 und kannst dementsprechend dem Server §cnicht beitreten§7!");
        });
    }

}
