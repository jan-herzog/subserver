package de.nebelniek.listeners;

import com.github.twitch4j.TwitchClient;
import de.nebelniek.ProxyConfiguration;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.services.twitch.TwitchSubscriptionService;
import de.nebelniek.services.verify.TwitchVerifyService;
import de.nebelniek.services.verify.VerifyService;
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

    private final TwitchVerifyService verifyService;
    private final TwitchSubscriptionService twitchSubscriptionService;
    private final CloudUserManagingService cloudUserRepository;
    private final TwitchClient twitchClient;

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        LuckPerms luckPerms = ProxyConfiguration.getLuckPerms();
        ProxiedPlayer player = event.getPlayer();
        cloudUserRepository.createUserIfNotExists(player.getUniqueId(), player.getName()).thenAccept(cloudUser -> {
            cloudUser.setLastLogin(new Date());
            cloudUser.saveAsync();
            twitchSubscriptionService.isSubbed(cloudUser);
            if (cloudUser.getTwitchId() == null)
                verifyService.showVerifySuggestion(player);
            if (luckPerms.getUserManager().getUser(cloudUser.getUuid()).getPrimaryGroup().equalsIgnoreCase("administrator"))
                return;
            if (cloudUser.getTwitchId() != null)
                if (twitchClient.getHelix().getModerators(TwitchTokens.NEBELNIEK.getToken(), TwitchTokens.NEBELNIEK.getChannelId(), null, null).execute().getModerators().stream().anyMatch(moderator -> moderator.getUserId().equalsIgnoreCase(cloudUser.getTwitchId()))) {
                    Group group = luckPerms.getGroupManager().getGroup("mod");
                    setGroup(group, cloudUser);
                    System.out.println(cloudUser.getTwitchId() + " mod");
                    return;
                }
            if (cloudUser.isSubbed()) {
                Group group = luckPerms.getGroupManager().getGroup("sub");
                setGroup(group, cloudUser);
                System.out.println(cloudUser.getTwitchId() + " Subbed");
            } else {
                Group group = luckPerms.getGroupManager().getGroup("default");
                setGroup(group, cloudUser);
                System.out.println(cloudUser.getTwitchId() + " Not subbed!");
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    private void setGroup(Group group, ICloudUser cloudUser) {
        LuckPerms luckPerms = ProxyConfiguration.getLuckPerms();
        CompletableFuture<Void> action = CompletableFuture.runAsync(() -> {
            luckPerms.getUserManager().loadUser(cloudUser.getUuid()).thenAccept(user -> {
                user.data().clear(NodeType.INHERITANCE::matches);
                Node node = InheritanceNode.builder(group).build();
                user.data().add(node);
            });
        });
        action.thenRunAsync(() -> {
            Optional<MessagingService> messagingService = luckPerms.getMessagingService();
            messagingService.ifPresent(service -> service.pushUserUpdate(luckPerms.getUserManager().getUser(cloudUser.getUuid())));
        });
    }

}
