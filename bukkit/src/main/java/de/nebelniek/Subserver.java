package de.nebelniek;

import de.nebelniek.web.BukkitSpringApplication;
import dev.alangomes.springspigot.SpringSpigotInitializer;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class Subserver extends JavaPlugin {

    private ConfigurableApplicationContext context;

    private BukkitConfiguration bukkitConfiguration;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ResourceLoader loader = new DefaultResourceLoader(getClassLoader());
        SpringApplication application = new SpringApplication(loader, BukkitSpringApplication.class);
        application.addInitializers(new SpringSpigotInitializer(this));
        context = application.run();
        bukkitConfiguration = context.getBean(BukkitConfiguration.class);
        bukkitConfiguration.startMinecraftPlugin(context, this);
    }

    @Override
    public void onDisable() {
        context.close();
        context = null;
    }
}
