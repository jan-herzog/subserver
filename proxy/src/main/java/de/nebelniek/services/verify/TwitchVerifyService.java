package de.nebelniek.services.verify;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;
import de.nebelniek.ProxyConfiguration;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.services.hashcode.TwitchHashcodeService;
import de.nebelniek.services.twitch.TwitchSubscriptionService;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TwitchVerifyService extends VerifyService {

    private final CloudUserManagingService cloudUserRepository;

    private final TwitchSubscriptionService twitchSubscriptionService;

    private final TwitchHashcodeService hashcodeService;

    private final TwitchClient twitchClient;

    private final ProxyConfiguration proxyConfiguration;

    public void showVerifySuggestion(ProxiedPlayer player) {
        hashcodeService.storeHash(player.getUniqueId());
        final TextComponent component = Component
                .text(Prefix.TWITCH + "Du bist noch nicht mit einem §5Twitch Account§7 verbunden! ")
                .append(
                        Component.text("Verbinde")
                                .color(NamedTextColor.YELLOW)
                                .style(Style.style(TextDecoration.BOLD))
                                .clickEvent(ClickEvent.openUrl("https://verify.nebelniek.de/auth?hash=" + hashcodeService.getHash(player.getUniqueId())))
                                .hoverEvent(
                                        Component.text("§a§lKlick!")
                                                .asHoverEvent()
                                )
                )
                .append(Component.text("§7 deinen §5Twitch Account§7, um zu §averifizieren§7, dass du §5Sub§7 bist."));
        proxyConfiguration.getAdventure().player(player).sendMessage(component);
    }

    public void showVerify(ProxiedPlayer player) {
        hashcodeService.storeHash(player.getUniqueId());
        final TextComponent component = Component
                .text(Prefix.TWITCH.getPrefix())
                .append(
                        Component.text("Klicke hier")
                                .color(NamedTextColor.YELLOW)
                                .style(Style.style(TextDecoration.BOLD))
                                .clickEvent(ClickEvent.openUrl("https://verify.nebelniek.de/auth?hash=" + hashcodeService.getHash(player.getUniqueId())))
                                .hoverEvent(Component.text("§a§lKlick!").asHoverEvent())
                )
                .append(Component.text("§7 um deinen §5Twitch Account§7 zu verbinden, um zu §averifizieren§7, dass du §5Sub§7 bist."));
        proxyConfiguration.getAdventure().player(player).sendMessage(component);
    }

    public void notifyPlayerIfOnline(UUID uuid, OAuth2Credential credential) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player == null) {
            LOGGER.debug("Notify cancelled because player (" + uuid + ") was not online!");
            return;
        }
        cloudUserRepository.loadUser(uuid).thenAccept(cloudUser -> {
            User user = twitchClient.getHelix().getUsers(credential.getAccessToken(), Collections.singletonList(cloudUser.getTwitchId()), null).execute().getUsers().get(0);
            player.sendMessage(Prefix.TWITCH + "Du wurdest §aerfolgreich§7 mit deinem Twitch-Account §5" + user.getLogin() + "§7 verbunden!");
            twitchSubscriptionService.notifyPlayer(player);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

}
