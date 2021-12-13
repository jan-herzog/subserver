package de.nebelniek.database.guild.util;

public record HomePoint(String world, double x, double y, double z) {

    public String string() {
        return "§e" + ((int) x) + "§7, §e" + ((int) y) + "§7, §e" + ((int) z);
    }

}
