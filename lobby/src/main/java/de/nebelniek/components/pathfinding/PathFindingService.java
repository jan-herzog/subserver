package de.nebelniek.components.pathfinding;

import de.nebelniek.configuration.LobbyConfiguration;
import org.bukkit.Bukkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PathFindingService {

    private final PathFindingPoint[] points = {
            new PathFindingPoint(0, 80,0)
    };

    private int i = 0;

    @Autowired
    public PathFindingService(LobbyConfiguration configuration) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(configuration.getPlugin(), () -> {
            if (i == points.length)
                i = 0;
            points[i].spawn();
            i++;
        }, 5L, 5L);
    }


}
