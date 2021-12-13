package de.nebelniek.components.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.domain.RedemptionStatus;
import com.github.twitch4j.helix.domain.CustomReward;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import de.nebelniek.content.coins.CoinsContentService;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.utils.Prefix;
import de.nebelniek.utils.TwitchTokens;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Service
public class ChannelPointRedemtionService {

    private final TwitchClient twitchClient;

    private final CloudUserManagingService cloudUserManagingService;

    private final CoinsContentService coinsContentService;

    @Autowired
    public ChannelPointRedemtionService(TwitchClient twitchClient, CloudUserManagingService cloudUserManagingService, CoinsContentService coinsContentService) {
        this.twitchClient = twitchClient;
        this.cloudUserManagingService = cloudUserManagingService;
        this.coinsContentService = coinsContentService;
        startListener();
    }

    private void startListener() {
        this.twitchClient.getPubSub().connect();
        this.twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(new OAuth2Credential("twitch", TwitchTokens.NEBELNIEK.getToken()), TwitchTokens.NEBELNIEK.getChannelId());
        this.twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, channelPointsRedemptionEvent());
        List<CustomReward> rewardList = twitchClient.getHelix().getCustomRewards(TwitchTokens.NEBELNIEK.getToken(), TwitchTokens.NEBELNIEK.getChannelId(), null, true).execute().getRewards();
        for (TwitchRewards value : TwitchRewards.values()) {
            if (rewardList.stream().noneMatch(customReward -> customReward.getTitle().equalsIgnoreCase(value.getName())))
                twitchClient.getHelix().createCustomReward(
                        TwitchTokens.NEBELNIEK.getToken(),
                        TwitchTokens.NEBELNIEK.getChannelId(),
                        CustomReward.builder().title(value.getName()).cost(value.getPrice()).backgroundColor("#0F0F0F").shouldRedemptionsSkipRequestQueue(false).prompt("Wenn du nicht weißt was das ist: !subserver | Du kannst diese Belohnung erst einlösen, wenn du auf dem Subserver verifiziert bist. (/verify twitch)").isUserInputRequired(false).build()
                ).execute();
        }
    }

    private Consumer<RewardRedeemedEvent> channelPointsRedemptionEvent() {
        return redeemedEvent -> {
            String userId = redeemedEvent.getRedemption().getUser().getId();
            TwitchRewards reward = null;
            for (TwitchRewards value : TwitchRewards.values())
                if (value.getName().equalsIgnoreCase(redeemedEvent.getRedemption().getReward().getTitle()))
                    reward = value;
            if (reward == null)
                return;
            TwitchRewards finalReward = reward;
            cloudUserManagingService.loadUserByTwitchId(userId).thenAccept(cloudUser -> {
                if (cloudUser == null) {
                    twitchClient.getChat().sendPrivateMessage(redeemedEvent.getRedemption().getUser().getLogin(), "Du musst zuerst deinen Twitch Account mit Hilfe von [/verify twitch] auf dem Minecraft-Server nebelniek.de verlinken, um Coins einlösen zu können! Wenn du jemanden deine Coins schenken willst, kannst du das dann auf dem Subserver machen.");
                    System.out.println("Invalid Twitch ID " + userId + " -> cancel");
                    setRedemptionStatus(redeemedEvent, RedemptionStatus.CANCELED);
                    return;
                }
                coinsContentService.addCoins(cloudUser, finalReward.getPrice());
                Player player = Bukkit.getPlayer(cloudUser.getUuid());
                if (player != null)
                    player.sendMessage(Prefix.COINS + "Du hast §5" + finalReward.getName() + " §7im Chat eingelöst. Dir wurden §e" + finalReward.getPrice() + " §7 gutgeschrieben.");
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        };
    }

    private void setRedemptionStatus(RewardRedeemedEvent event, RedemptionStatus status) {
        twitchClient.getHelix().updateRedemptionStatus(
                TwitchTokens.NEBELNIEK.getToken(),
                TwitchTokens.NEBELNIEK.getChannelId(),
                event.getRedemption().getReward().getId(),
                Collections.singletonList(event.getRedemption().getId()),
                status
        ).execute();
    }

}
