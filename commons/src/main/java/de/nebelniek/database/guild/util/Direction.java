package de.nebelniek.database.guild.util;

import de.nebelniek.database.guild.interfaces.IRegion;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Direction {

    NORTH("Norden"),
    EAST("Osten"),
    SOUTH("Süden"),
    WEST("Westen");

    private String prettyName;

    public int getBlocks(IRegion region, int amplifier) {
        switch (this) {
            case NORTH, SOUTH -> {
                return (int) (Math.abs(region.getBX() - region.getAX()) * amplifier);
            }
            case EAST, WEST -> {
                return (int) (Math.abs(region.getBZ() - region.getAZ()) * amplifier);
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return this.prettyName;
    }
}
