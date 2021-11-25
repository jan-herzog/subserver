package de.nebelniek.components.pathfinding;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public record PathFindingPoint(double x, double y, double z) {

    public void spawn() {
        World world = Bukkit.getWorld("world");
        Location location = new Location(world, x, y, z);
        world.spawnParticle(Particle.FIREWORKS_SPARK, location, 1);
    }

}
