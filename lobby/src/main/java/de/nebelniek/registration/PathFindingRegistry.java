package de.nebelniek.registration;

import de.nebelniek.components.pathfinding.PathFindingService;
import de.nebelniek.registration.event.BukkitPluginEnableEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PathFindingRegistry {

    private final PathFindingService pathFindingService;

    @EventListener
    public void loadOnEnable(BukkitPluginEnableEvent event) {
        pathFindingService.start(event.getPlugin());
    }
}
