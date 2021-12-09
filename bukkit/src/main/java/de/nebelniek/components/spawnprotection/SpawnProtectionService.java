package de.nebelniek.components.spawnprotection;

import de.nebelniek.database.guild.Region;
import org.bukkit.Location;
import org.springframework.stereotype.Service;

@Service
public class SpawnProtectionService {

    public boolean isNearSpawn(Location location) {
        Region region = WorldSpawns.getByLocation(location).getRegion();
        return region.doesCollide(location.getX() - 20, location.getZ() - 20, location.getX() + 20, location.getZ() + 20);
    }

    public boolean isInSpawn(Location location) {
        Region region = WorldSpawns.getByLocation(location).getRegion();
        return region.isIn(location.getX(), location.getZ());
    }
}