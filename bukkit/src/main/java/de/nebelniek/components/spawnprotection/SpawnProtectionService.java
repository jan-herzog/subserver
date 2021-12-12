package de.nebelniek.components.spawnprotection;

import de.nebelniek.database.guild.Region;
import org.bukkit.Location;
import org.springframework.stereotype.Service;

@Service
public class SpawnProtectionService {

    public boolean isNearSpawn(Location location) {
        WorldSpawns worldSpawns = WorldSpawns.getByLocation(location);
        if(worldSpawns == null)
            return false;
        return worldSpawns.getRegion().doesCollide(location.getWorld().getName(), location.getX() - 20, location.getZ() - 20, location.getX() + 20, location.getZ() + 20);
    }

    public boolean isInSpawn(Location location) {
        WorldSpawns worldSpawns = WorldSpawns.getByLocation(location);
        if(worldSpawns == null)
            return false;
        return worldSpawns.getRegion().isIn(location.getWorld().getName(), location.getX(), location.getZ());
    }
}