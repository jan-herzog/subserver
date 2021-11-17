package de.nebelniek.utils;

public record HexColors(String main, String accent) {
    public static String toColor(String hex) {
        return "§x§" + hex.charAt(1) + "§" + hex.charAt(2) + "§" + hex.charAt(3) + "§" + hex.charAt(4) + "§" + hex.charAt(5) + "§" + hex.charAt(6);
    }
}
