package de.nebelniek;

import co.aikar.commands.BungeeCommandManager;
import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import de.nebelniek.registration.ProxyPluginEnableEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import de.nebelniek.web.controller.HomeController;
import de.nebelniek.web.controller.VerifyController;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
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
        context.getBean(VerifyController.class).setupRoutes();
        context.getBean(HomeController.class).setupRoutes();
        this.adventure = BungeeAudiences.create(plugin);
    }

    @Setter
    private BungeeCommandManager commandManager;

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
        credentialManager.registerIdentityProvider(new TwitchIdentityProvider("7suv1m3ae2vbiqjpbn5n2ovlnta440", "6jna6vduaf03rmh1npzk7j4q7knsxy", "https://verify.nebelniek.de/callback"));
        return credentialManager.getOAuth2IdentityProviderByName("twitch").get();
    }

}
