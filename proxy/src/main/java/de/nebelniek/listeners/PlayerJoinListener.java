package de.nebelniek.listeners;

import com.github.twitch4j.TwitchClient;
import de.nebelniek.ProxyConfiguration;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.services.permission.RankUpdateService;
import de.nebelniek.services.twitch.TwitchSubscriptionService;
import de.nebelniek.services.verify.TwitchVerifyService;
import de.nebelniek.utils.TwitchTokens;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.messaging.MessagingService;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PlayerJoinListener implements Listener {

    private final CloudUserManagingService cloudUserRepository;

    private final RankUpdateService rankUpdateService;

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        cloudUserRepository.createUserIfNotExists(player.getUniqueId(), player.getName()).thenAccept(cloudUser -> {
            cloudUser.setLastLogin(new Date());
            cloudUser.saveAsync();
            rankUpdateService.check(cloudUser, player);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

}
