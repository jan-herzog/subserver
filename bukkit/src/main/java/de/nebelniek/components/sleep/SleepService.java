package de.nebelniek.components.sleep;

import de.nebelniek.components.tablistchat.utils.NameUtils;
import de.nebelniek.utils.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SleepService {

    private final ArrayList<Player> sleeping = new ArrayList<>();

    @EventHandler
    public void on(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        if (!event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK))
            return;
        sleeping.add(player);
        Bukkit.broadcastMessage(Prefix.SUBSERVER + NameUtils.getColoredName(player.getUniqueId(), player.getName()) + " §7schläft nun! §8(§6" + getPercent() + "§7%/25%§8)");
        if (getPercent() >= 25) {
            Bukkit.broadcastMessage(Prefix.SUBSERVER + "§7Die Nacht wurde §aübersprungen§7!");
            Bukkit.getWorld("world").setTime(0L);
        }
    }

    @EventHandler
    public void on(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        sleeping.remove(player);
        if (!(Bukkit.getWorld("world").getTime() <= 10L))
            Bukkit.broadcastMessage(Prefix.SUBSERVER + NameUtils.getColoredName(player.getUniqueId(), player.getName()) + " §7schläft nun §cnicht mehr§7! §8(§6" + getPercent() + "§7%/25%§8)");
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        sleeping.remove(player);
        if (getPercent() >= 50) {
            Bukkit.broadcastMessage(Prefix.SUBSERVER + "§7Die Nacht wurde §aübersprungen§7!");
            Bukkit.getWorld("world").setTime(0L);
        }
    }

    private double getPercent() {
        int size = 0;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            if (onlinePlayer.getWorld().getName().equalsIgnoreCase("world"))
                if (!onlinePlayer.isDead())
                    size++;
        double p = 100.0D / size;
        return Math.round(p * this.sleeping.size());
    }

}
