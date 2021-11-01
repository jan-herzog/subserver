package de.nebelniek;

import de.nebelniek.web.BukkitSpringApplication;
import dev.alangomes.springspigot.SpringSpigotBootstrapper;
import dev.alangomes.springspigot.SpringSpigotInitializer;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Properties;

public class Subserver extends JavaPlugin {

    private ClassLoader defaultClassLoader;
    private ConfigurableApplicationContext context;

    private BukkitConfiguration bukkitConfiguration;

    @SneakyThrows
    @Override
    public void onEnable() {
        defaultClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClassLoader());
        context = SpringSpigotBootstrapper.initialize(this, BukkitSpringApplication.class);
        bukkitConfiguration = context.getBean(BukkitConfiguration.class);
        bukkitConfiguration.startMinecraftPlugin(context, this);

        init();
    }

    private void init() throws IOException {
        Properties props = new Properties();
        props.load(getClassLoader().getResourceAsStream("application.properties"));

        SpringApplication application = new SpringApplication(BukkitSpringApplication.class);
        application.setDefaultProperties(props);
    }

    @Override
    public void onDisable() {
        Thread.currentThread().setContextClassLoader(defaultClassLoader);
        context.close();
        context = null;
    }
}
