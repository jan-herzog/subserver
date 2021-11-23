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
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.GuildAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
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

    @Getter
    private static Guild mainGuild;

    public void startBukkitPlugin(ApplicationContext context, JavaPlugin plugin) {
        this.eventPublisher.publishEvent(new BukkitPluginEnableEvent(context, plugin));
        mainGuild = context.getBean(JDA.class).getGuildById("911963860242673734");
        this.plugin = plugin;
    }

    @Setter
    private PaperCommandManager commandManager;

    @Setter
    private InventoryManager inventoryManager;

    @Bean
    @SneakyThrows
    public JDA buildJDA() {
        return JDABuilder.createDefault("OTA3Mzk4MjUxNzE0NjA1MDU3.YYmmeQ.Wfy_wZgPorvT-KkqsZQmO5GUMPA")
                .setStatus(OnlineStatus.ONLINE)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.playing("nebelniek.de"))
                .build();
    }

    @Bean
    @DependsOn("buildOAuth2IdentityProvider")
    public TwitchClient buildTwitchClient() {
        return TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnablePubSub(true)
                .withEnableChat(true)
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
