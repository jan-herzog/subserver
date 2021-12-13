package de.nebelniek.services.verify;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;
import de.nebelniek.ProxyConfiguration;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.services.hashcode.DiscordHashcodeService;
import de.nebelniek.services.hashcode.HashcodeService;
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

public abstract class VerifyService {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public abstract void showVerifySuggestion(ProxiedPlayer player);

    public abstract void showVerify(ProxiedPlayer player);

}
