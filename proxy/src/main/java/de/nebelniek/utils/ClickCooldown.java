package de.nebelniek.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ClickCooldown {

    private static Map<UUID, Long> lastClicks = new HashMap<>();

    public static void registerClick(UUID uuid) {
        if (lastClicks.get(uuid) == null)
            lastClicks.put(uuid, System.currentTimeMillis());
        else lastClicks.replace(uuid, System.currentTimeMillis());
    }

    public static boolean isAbleToClick(UUID uuid) {
        if (lastClicks.get(uuid) == null)
            return true;
        return System.currentTimeMillis() >= lastClicks.get(uuid) + TimeUnit.SECONDS.toMillis(5);
    }

    public static int getCooldown(UUID uuid) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(lastClicks.get(uuid) + TimeUnit.SECONDS.toMillis(5) - System.currentTimeMillis());
    }

}
