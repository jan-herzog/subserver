package de.nebelniek.registration;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;


@Getter
public class ProxyPluginEnableEvent extends ApplicationEvent {

    private final ApplicationContext applicationContext;
    private final Plugin plugin;

    public ProxyPluginEnableEvent(ApplicationContext applicationContext, Plugin plugin) {
        super(applicationContext);
        this.applicationContext = applicationContext;
        this.plugin = plugin;
    }
}
