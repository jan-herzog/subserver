package de.nebelniek.configuration;

import co.aikar.commands.PaperCommandManager;
import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import de.nebelniek.registration.event.BukkitPluginEnableEvent;
import de.notecho.inventory.InventoryManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Getter
@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BukkitConfiguration {

    private final ApplicationEventPublisher eventPublisher;

    private CredentialManager credentialManager;

    @Getter
    private JavaPlugin plugin;

    public void startBukkitPlugin(ApplicationContext context, JavaPlugin plugin) {
        this.eventPublisher.publishEvent(new BukkitPluginEnableEvent(context, plugin));
        this.plugin = plugin;
    }

    @Setter
    private PaperCommandManager commandManager;

    @Setter
    private InventoryManager inventoryManager;

    @Bean
    @DependsOn("buildOAuth2IdentityProvider")
    public TwitchClient buildTwitchClient() {
        return TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnablePubSub(true)
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