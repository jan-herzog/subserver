package de.nebelniek.registration;

import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;


@Getter
public class BukkitPluginEnableEvent extends ApplicationEvent {

    private final ApplicationContext applicationContext;
    private final JavaPlugin javaPlugin;

    public BukkitPluginEnableEvent(ApplicationContext applicationContext, JavaPlugin javaPlugin) {
        super(applicationContext);
        this.applicationContext = applicationContext;
        this.javaPlugin = javaPlugin;
    }
}
