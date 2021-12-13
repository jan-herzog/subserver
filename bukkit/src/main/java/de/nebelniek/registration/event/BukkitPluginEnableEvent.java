package de.nebelniek.registration.event;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

@Getter
public class BukkitPluginEnableEvent extends ApplicationEvent {

    private final ApplicationContext applicationContext;
    private final JavaPlugin plugin;

    public BukkitPluginEnableEvent(ApplicationContext applicationContext, JavaPlugin plugin) {
        super(applicationContext);
        this.applicationContext = applicationContext;
        this.plugin = plugin;
    }

}
