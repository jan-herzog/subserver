package de.nebelniek.configuration;

import de.nebelniek.registration.event.BukkitPluginEnableEvent;
import de.slikey.effectlib.EffectManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LobbyConfiguration {

    private final ApplicationEventPublisher eventPublisher;

    @Getter
    private JavaPlugin plugin;

    @Getter
    private EffectManager effectManager;


    public void startBukkitPlugin(ApplicationContext context, JavaPlugin plugin) {
        this.plugin = plugin;
        this.effectManager = new EffectManager(this.plugin);
        this.eventPublisher.publishEvent(new BukkitPluginEnableEvent(context, plugin));
    }

    public Location getSpawnLocation() {
        return new Location(Bukkit.getWorld("world"), 0, 80, 0);
    }

}
