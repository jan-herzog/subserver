package de.nebelniek.components.spawnprotection;

import de.nebelniek.database.guild.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

@AllArgsConstructor
@Getter
public enum WorldSpawns {

    OVERWORLD(new Region("world", 40, 40, -40, -40)),
    NETHER(new Region("world_nether", 0, 0, 0, 0)),
    END(new Region("world_the_end", -74, 74, 140, -120)),
    ;

    private final Region region;

    public static WorldSpawns getByLocation(Location location) {
        return getByName(location.getWorld().getName());
    }

    public static WorldSpawns getByName(String name) {
        switch (name) {
            case "world" -> {
                return OVERWORLD;
            }
            case "world_nether" -> {
                return null;
            }
            case "world_the_end" -> {
                return END;
            }
        }
        return null;
    }

}
