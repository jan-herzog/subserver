package de.nebelniek;

import co.aikar.commands.BungeeCommandManager;
import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import de.nebelniek.registration.ProxyPluginEnableEvent;
import de.nebelniek.utils.HexColors;
import de.nebelniek.web.controller.HomeController;
import de.nebelniek.web.controller.verify.DiscordVerifyController;
import de.nebelniek.web.controller.verify.TwitchVerifyController;
import io.mokulu.discord.oauth.DiscordOAuth;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.plugin.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import static spark.Spark.port;

@Getter
@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProxyConfiguration {

    private final ApplicationEventPublisher eventPublisher;

    private CredentialManager credentialManager;

    private BungeeAudiences adventure;

    public void startProxyPlugin(ApplicationContext context, Plugin plugin) {
        this.eventPublisher.publishEvent(new ProxyPluginEnableEvent(context, plugin));
        port(4556);
        context.getBean(TwitchVerifyController.class).setupRoutes();
        context.getBean(DiscordVerifyController.class).setupRoutes();
        context.getBean(HomeController.class).setupRoutes();
        this.adventure = BungeeAudiences.create(plugin);
    }

    @Setter
    private BungeeCommandManager commandManager;

    @Setter
    @Getter
    private static LuckPerms luckPerms;


    @SneakyThrows
    @Bean
    public DiscordOAuth buildDiscordOAuth() {
        return new DiscordOAuth("907398251714605057", "oTp8tqOmmkjWevmczVmWFgfJP5MzjcPz", "https://verify.nebelniek.de/callback/discord", new String[]{"identify", "guilds.join"});
    }

    @Bean
    @DependsOn("buildOAuth2IdentityProvider")
    public TwitchClient buildTwitchClient() {
        return TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withCredentialManager(credentialManager)
                .build();
    }



    @Bean
    public OAuth2IdentityProvider buildOAuth2IdentityProvider() {
        this.credentialManager = CredentialManagerBuilder.builder().build();
        credentialManager.registerIdentityProvider(new TwitchIdentityProvider("7suv1m3ae2vbiqjpbn5n2ovlnta440", "6jna6vduaf03rmh1npzk7j4q7knsxy", "https://verify.nebelniek.de/callback/twitch"));
        return credentialManager.getOAuth2IdentityProviderByName("twitch").get();
    }

    @Bean
    public HexColors buildHexColors() {
        return new HexColors(HexColors.toColor("#d0ff00"), HexColors.toColor("#ffb300"));
    }

}
