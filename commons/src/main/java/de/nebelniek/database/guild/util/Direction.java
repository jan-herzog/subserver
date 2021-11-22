package de.nebelniek.database.guild.util;

import de.nebelniek.database.guild.interfaces.IRegion;

public enum Direction {

    NORTH, EAST, SOUTH, WEST;

    public int getBlocks(IRegion region, int amplifier) {
        switch (this) {
            case NORTH, SOUTH -> {
                return (int) (region.getBX() - region.getAX() * 10);
            }
            case EAST, WEST -> {
                return (int) (region.getBZ() - region.getAZ() * 10);
            }
        }
        return -1;
    }

}
