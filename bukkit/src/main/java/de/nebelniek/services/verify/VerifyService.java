package de.nebelniek.services.verify;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;
import de.nebelniek.database.user.CloudUser;
import de.nebelniek.database.user.CloudUserRepository;
import de.nebelniek.services.hashcode.HashcodeService;
import de.nebelniek.services.twitch.TwitchSubscriptionService;
import de.nebelniek.utils.Prefix;
import de.nebelniek.utils.TwitchTokens;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class VerifyService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private CloudUserRepository cloudUserRepository;

    private TwitchSubscriptionService twitchSubscriptionService;

    private HashcodeService hashcodeService;

    private TwitchClient twitchClient;

    public void showVerifySuggestion(Player player) {
        hashcodeService.storeHash(player.getUniqueId());
        final TextComponent component = Component
                .text(Prefix.TWITCH + "Du bist noch nicht mit einem §5Twitch Account§7 verbunden!")
                .append(
                        Component.text("Verbinde")
                                .color(NamedTextColor.YELLOW)
                                .style(Style.style(TextDecoration.BOLD))
                                .hoverEvent(
                                        Component.text("§a§lKlick!")
                                                .clickEvent(ClickEvent.openUrl("https://verify.nebelniek.de/auth?hash=" + hashcodeService.getHash(player.getUniqueId())))
                                                .asHoverEvent()
                                )
                )
                .append(Component.text("§7 deinen §5Twitch Account§7, um zu §averifizieren§7, dass du §5Sub§7 bist."));
        player.sendMessage(component);
    }

    public void showVerify(Player player) {
        hashcodeService.storeHash(player.getUniqueId());
        final TextComponent component = Component
                .text(Prefix.TWITCH.getPrefix())
                .append(
                        Component.text("Verbinde")
                                .color(NamedTextColor.YELLOW)
                                .style(Style.style(TextDecoration.BOLD))
                                .hoverEvent(
                                        Component.text("§a§lKlick!")
                                                .clickEvent(ClickEvent.openUrl("https://verify.nebelniek.de/auth?hash=" + hashcodeService.getHash(player.getUniqueId())))
                                                .asHoverEvent()
                                )
                )
                .append(Component.text("§7 deinen §5Twitch Account§7, um zu §averifizieren§7, dass du §5Sub§7 bist."));
        player.sendMessage(component);
    }

    public void notifyPlayerIfOnline(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            LOGGER.debug("Notify cancelled because player (" + uuid + ") was not online!");
            return;
        }
        CloudUser cloudUser = cloudUserRepository.findByUuidIs(uuid);
        User user = twitchClient.getHelix().getUsers(TwitchTokens.HELIXTOKEN.getToken(), Collections.singletonList(cloudUser.getTwitchId()), null).execute().getUsers().get(0);
        player.sendMessage(Prefix.TWITCH + "Du wurdest §aerfolgreich§7 mit deinem Twitch-Account §5" + user.getLogin() + "§7 verbunden!");
        twitchSubscriptionService.notifyPlayer(player);
    }

}
