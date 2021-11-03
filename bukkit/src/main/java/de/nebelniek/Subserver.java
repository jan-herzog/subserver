package de.nebelniek;

import de.nebelniek.web.BukkitSpringApplication;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.Properties;

public class Subserver extends JavaPlugin {

    private ClassLoader defaultClassLoader;
    private ConfigurableApplicationContext context;

    @SneakyThrows
    @Override
    public void onEnable() {
        context = new AnnotationConfigApplicationContext(BukkitSpringApplication.class);
        BukkitConfiguration bukkitConfiguration = context.getBean(BukkitConfiguration.class);
        bukkitConfiguration.startMinecraftPlugin(context, this);
        context.registerShutdownHook();
    }

    private void init() throws IOException {
        Properties props = new Properties();
        props.load(getClassLoader().getResourceAsStream("application.properties"));

        SpringApplication application = new SpringApplication(BukkitSpringApplication.class);
        application.setDefaultProperties(props);
    }

    @Override
    public void onDisable() {
        context.close();
        context = null;
    }
}
