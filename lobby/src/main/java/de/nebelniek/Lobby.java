package de.nebelniek;

import de.nebelniek.application.LobbySpringApplication;
import de.nebelniek.configuration.LobbyConfiguration;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Lobby extends JavaPlugin {

    @Getter
    private static AnnotationConfigApplicationContext context;

    @Override
    public void onEnable() {
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        context = new AnnotationConfigApplicationContext(LobbySpringApplication.class);
        LobbyConfiguration bukkitConfiguration = context.getBean(LobbyConfiguration.class);
        bukkitConfiguration.startBukkitPlugin(context, this);
        context.registerShutdownHook();
    }
}
