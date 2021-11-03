package de.nebelniek;

import co.aikar.commands.PaperCommandManager;
import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import de.nebelniek.registration.BukkitPluginEnableEvent;
import de.nebelniek.web.controller.HomeController;
import de.nebelniek.web.controller.VerifyController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Getter
@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BukkitConfiguration {

    private final ApplicationEventPublisher eventPublisher;

    private final HomeController homeController;
    private final VerifyController verifyController;

    private CredentialManager credentialManager;

    public void startMinecraftPlugin(ApplicationContext context, JavaPlugin plugin) {
        this.eventPublisher.publishEvent(new BukkitPluginEnableEvent(context, plugin));
        this.homeController.setupRoutes();
        this.verifyController.setupRoutes();
    }

    @Setter
    private PaperCommandManager commandManager;

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
        credentialManager.registerIdentityProvider(new TwitchIdentityProvider("7suv1m3ae2vbiqjpbn5n2ovlnta440", "6jna6vduaf03rmh1npzk7j4q7knsxy", "https://verify.nebelniek.de/callback/"));
        return credentialManager.getOAuth2IdentityProviderByName("twitch").get();
    }

    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("de.nebelniek.database.user");
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MariaDB103Dialect");
        sessionFactory.setHibernateProperties(properties);
        return sessionFactory;
    }

    private DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource.setUsername("out");
        dataSource.setPassword("polen1hzg");
        dataSource.setUrl("jdbc:mariadb://notecho.de:3306/backend?createDatabaseIfNotExist=true");
        return dataSource;
    }
}
