package de.nebelniek.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;


@Getter
public class BukkitPluginEnableEvent extends ApplicationEvent {

    private ApplicationContext applicationContext;
    private JavaPlugin javaPlugin;
    private PluginManager pluginManager;

    public BukkitPluginEnableEvent(ApplicationContext applicationContext, JavaPlugin javaPlugin, PluginManager pluginManager) {
        super(applicationContext);
        this.applicationContext = applicationContext;
        this.javaPlugin = javaPlugin;
        this.pluginManager = pluginManager;
    }
}
