package de.nebelniek;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import de.nebelniek.registration.BukkitPluginEnableEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Getter
@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BukkitConfiguration {

    private final ApplicationEventPublisher eventPublisher;

    public void startMinecraftPlugin() {
        this.eventPublisher.publishEvent(new BukkitPluginEnableEvent(null, null, null));
    }

    @Bean
    private TwitchClient buildTwitchClient() {
        return TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .build();
    }

    @Bean
    private OAuth2IdentityProvider buildOAuth2IdentityProvider() {
        final CredentialManager credentialManager = CredentialManagerBuilder.builder().build();
        credentialManager.registerIdentityProvider(new TwitchIdentityProvider("7suv1m3ae2vbiqjpbn5n2ovlnta440", "6jna6vduaf03rmh1npzk7j4q7knsxy", "https://verify.nebelniek.de/callback/"));
        return credentialManager.getOAuth2IdentityProviderByName("twitch").get();
    }

}
