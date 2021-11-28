package de.nebelniek.components.pathfinding;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.stereotype.Service;

@Service
public class PathFindingService {

    private final PathFindingPoint[] points = {
            new PathFindingPoint(95, 7, 110),
            new PathFindingPoint(94, 6.5, 110),
            new PathFindingPoint(93, 6, 112),
            new PathFindingPoint(92, 6, 113),
            new PathFindingPoint(91, 6, 114),
            new PathFindingPoint(90, 6, 115),
            new PathFindingPoint(89, 6, 116),
            new PathFindingPoint(88, 6, 117),
            new PathFindingPoint(87, 6, 117),
            new PathFindingPoint(86, 6, 118),
            new PathFindingPoint(85, 6, 119),
            new PathFindingPoint(84, 6, 120),
            new PathFindingPoint(83, 6, 120),
            new PathFindingPoint(82, 6, 121),
            new PathFindingPoint(82, 6, 121),
            new PathFindingPoint(81, 6, 122),
            new PathFindingPoint(80, 6, 123),
            new PathFindingPoint(79, 6, 124),
            new PathFindingPoint(78, 6, 125),
            new PathFindingPoint(77, 6, 126)
    };

    private int i = 0;

    public void start(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (i == points.length)
                i = 0;
            points[i].spawn();
            i++;
        }, 1L, 1L);
    }


}
