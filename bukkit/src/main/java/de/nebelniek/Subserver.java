package de.nebelniek;

import de.nebelniek.database.user.CloudUserRepository;
import de.nebelniek.listeners.PlayerJoinListener;
import de.nebelniek.registration.BukkitListenerRegistry;
import de.nebelniek.registration.CommandRegistry;
import de.nebelniek.services.hashcode.HashcodeService;
import de.nebelniek.services.twitch.TwitchSubscriptionService;
import de.nebelniek.services.verify.VerifyService;
import de.nebelniek.web.controller.HomeController;
import de.nebelniek.web.controller.VerifyController;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Properties;

public class Subserver extends JavaPlugin {

    private AnnotationConfigApplicationContext context;

    @SneakyThrows
    @Override
    public void onEnable() {
        Thread.currentThread().setContextClassLoader(getClassLoader());
        context = new AnnotationConfigApplicationContext(BukkitSpringApplication.class);
        System.out.println(context.getBeanDefinitionCount());
        for (String beanName : context.getBeanDefinitionNames()) {
            System.out.println(beanName);
        }
        BukkitConfiguration bukkitConfiguration = context.getBean(BukkitConfiguration.class);
        bukkitConfiguration.startMinecraftPlugin(context, this);
        context.registerShutdownHook();
    }

    @Override
    public void onDisable() {
        context.close();
        context = null;
    }
}
