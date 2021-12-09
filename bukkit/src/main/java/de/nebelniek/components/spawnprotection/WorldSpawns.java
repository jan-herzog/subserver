package de.nebelniek.components.spawnprotection;

import de.nebelniek.database.guild.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

@AllArgsConstructor
@Getter
public enum WorldSpawns {

    OVERWORLD(new Region(0,0,0,0)),
    NETHER(new Region(0,0,0,0)),
    END(new Region(0,0,0,0)),
    ;

    private final Region region;

    public static WorldSpawns getByLocation(Location location) {
        switch (location.getWorld().getName()) {
            case "world" -> {
                return OVERWORLD;
            }
            case "world_nether" -> {
                return NETHER;
            }
            case "world_the_end" -> {
                return END;
            }
        }
        return null;
    }

}
