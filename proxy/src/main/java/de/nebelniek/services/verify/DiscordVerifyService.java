package de.nebelniek.services.verify;

import de.nebelniek.ProxyConfiguration;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.services.hashcode.DiscordHashcodeService;
import de.nebelniek.utils.Prefix;
import io.mokulu.discord.oauth.model.User;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DiscordVerifyService extends VerifyService {

    private final CloudUserManagingService cloudUserManagingService;

    private final DiscordHashcodeService hashcodeService;

    private final ProxyConfiguration proxyConfiguration;


    public void showVerifySuggestion(ProxiedPlayer player) {
        hashcodeService.storeHash(player.getUniqueId());
        final TextComponent component = Component
                .text(Prefix.DISCORD + "Du bist noch nicht mit einem §9Discord Account§7 verbunden! ")
                .append(
                        Component.text("Verbinde")
                                .color(NamedTextColor.YELLOW)
                                .style(Style.style(TextDecoration.BOLD))
                                .clickEvent(ClickEvent.openUrl("https://verify.nebelniek.de/discord/auth?hash=" + hashcodeService.getHash(player.getUniqueId())))
                                .hoverEvent(
                                        Component.text("§a§lKlick!")
                                                .asHoverEvent()
                                )
                )
                .append(Component.text("§7 deinen §9Discord Account§7, um auf exklusive §aGilden-Channel§7 auf dem §5Subserver Discord§7 zugreifen zu können."));
        proxyConfiguration.getAdventure().player(player).sendMessage(component);
    }

    public void showVerify(ProxiedPlayer player) {
        hashcodeService.storeHash(player.getUniqueId());
        final TextComponent component = Component
                .text(Prefix.DISCORD.getPrefix())
                .append(
                        Component.text("Klicke hier")
                                .color(NamedTextColor.YELLOW)
                                .style(Style.style(TextDecoration.BOLD))
                                .clickEvent(ClickEvent.openUrl("https://verify.nebelniek.de/discord/auth?hash=" + hashcodeService.getHash(player.getUniqueId())))
                                .hoverEvent(Component.text("§a§lKlick!").asHoverEvent())
                )
                .append(Component.text("§7 um deinen §9Discord Account§7 zu verbinden, um auf exklusive §aGilden-Channel§7 auf dem §9Subserver Discord§7 zugreifen zu können."));
        proxyConfiguration.getAdventure().player(player).sendMessage(component);
    }

    public void notifyPlayerIfOnline(UUID uuid, User user) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player == null) {
            LOGGER.debug("Notify cancelled because player (" + uuid + ") was not online!");
            return;
        }
        cloudUserManagingService.loadUser(uuid).thenAccept(cloudUser -> {
            player.sendMessage(Prefix.DISCORD + "Du wurdest §aerfolgreich§7 mit deinem Discord-Account §9" + user.getFullUsername() + "§7 verbunden!");
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

}
