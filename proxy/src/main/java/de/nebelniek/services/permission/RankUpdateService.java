package de.nebelniek.services.permission;

import com.github.twitch4j.TwitchClient;
import de.nebelniek.ProxyConfiguration;
import de.nebelniek.database.user.interfaces.ICloudUser;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RankUpdateService {

    private final TwitchSubscriptionService twitchSubscriptionService;

    private final TwitchClient twitchClient;

    public void check(ICloudUser cloudUser, ProxiedPlayer player) {
        LuckPerms luckPerms = ProxyConfiguration.getLuckPerms();
        twitchSubscriptionService.checkIfSubbed(cloudUser).thenAccept(is -> {
            if (luckPerms.getUserManager().getUser(cloudUser.getUuid()).getPrimaryGroup().equalsIgnoreCase("administrator"))
                return;
            if (luckPerms.getUserManager().getUser(cloudUser.getUuid()).getPrimaryGroup().equalsIgnoreCase("team"))
                return;
            if (cloudUser.getTwitchId() != null) {
                if ((long) twitchClient.getHelix().getModerators(TwitchTokens.NEBELNIEK.getToken(), TwitchTokens.NEBELNIEK.getChannelId(), Collections.singletonList(cloudUser.getTwitchId()), null, 1).execute().getModerators().size() == 1) {
                    Group group = luckPerms.getGroupManager().getGroup("mod");
                    setGroup(group, cloudUser);
                    System.out.println(cloudUser.getTwitchId() + " mod");
                    return;
                }
            }
            if (is) {
                Group group = luckPerms.getGroupManager().getGroup("sub");
                setGroup(group, cloudUser);
                System.out.println(cloudUser.getTwitchId() + " Subbed");
            } else {
                Group group = luckPerms.getGroupManager().getGroup("default");
                setGroup(group, cloudUser);
                System.out.println(cloudUser.getTwitchId() + " Not subbed!");
            }
        });
    }


    private void setGroup(Group group, ICloudUser cloudUser) {
        LuckPerms luckPerms = ProxyConfiguration.getLuckPerms();
        luckPerms.getUserManager().modifyUser(cloudUser.getUuid(), user -> {
            user.data().clear(NodeType.INHERITANCE::matches);
            Node node = InheritanceNode.builder(group).build();
            user.data().add(node);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        }).thenRunAsync(() -> {
            Optional<MessagingService> messagingService = luckPerms.getMessagingService();
            messagingService.ifPresent(service -> service.pushUserUpdate(luckPerms.getUserManager().getUser(cloudUser.getUuid())));
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }


}
