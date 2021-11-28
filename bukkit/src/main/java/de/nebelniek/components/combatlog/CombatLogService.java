package de.nebelniek.components.combatlog;

import de.nebelniek.components.tablistchat.utils.NameUtils;
import de.nebelniek.configuration.BukkitConfiguration;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CombatLogService implements Listener {

    private final HashMap<ICloudUser, Long> cache = new HashMap<>();

    private final CloudUserManagingService cloudUserManagingService;

    private final GuildManagingService guildManagingService;

    public boolean isInFight(ICloudUser cloudUser) {
        return cache.containsKey(cloudUser);
    }

    public void start(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            List<ICloudUser> cloudUsers = new ArrayList<>();
            cache.forEach((cloudUser, aLong) -> cloudUsers.add(cloudUser));
            for (ICloudUser cloudUser : cloudUsers) {
                Player player = Bukkit.getPlayer(cloudUser.getUuid());
                if (cache.get(cloudUser) + TimeUnit.SECONDS.toMillis(60) <= System.currentTimeMillis()) {
                    if (player != null)
                        player.sendMessage(Prefix.COMBAT + "Du bist nun §anicht mehr§7 im Kampf und §akannst§7 dich nun ausloggen.");
                    cache.remove(cloudUser);
                    continue;
                }
                if (player != null)
                    player.sendActionBar(Prefix.COMBAT + "§4Du bist im Kampf! §7Cooldown §8» §c" + TimeUnit.MILLISECONDS.toSeconds((cache.get(cloudUser) + TimeUnit.SECONDS.toMillis(60)) - System.currentTimeMillis()) + "§7s");
            }
        }, 20L, 20L);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player))
            return;
        Player reciever = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        ICloudUser cRevceiver = cloudUserManagingService.getCloudUsers().get(reciever.getUniqueId());
        ICloudUser cDamager = cloudUserManagingService.getCloudUsers().get(damager.getUniqueId());
        if (!isInFight(cRevceiver))
            if (cDamager.getGuild() != cRevceiver.getGuild())
                if (guildManagingService.getGuildAt(reciever.getLocation().getX(), reciever.getLocation().getZ()).equals(cRevceiver.getGuild())) {
                    damager.sendMessage(Prefix.COMBAT + "§cDieser Spieler steht auf seinem Grundstück! Du kannst ihn nicht angreifen!");
                    event.setCancelled(true);
                }
        updatePlayer(reciever);
        updatePlayer(damager);
    }

    private void updatePlayer(Player player) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(player.getUniqueId());
        if (cache.containsKey(cloudUser)) {
            cache.replace(cloudUser, System.currentTimeMillis());
            return;
        }
        cache.put(cloudUser, System.currentTimeMillis());
        player.sendMessage(Prefix.COMBAT + "§cDu bist nun im Kampf. §7Bitte logge dich §c§lNICHT §7aus!");
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(player.getUniqueId());
        if (!cache.containsKey(cloudUser))
            return;
        if (player.getHealth() <= 0)
            return;
        player.setHealth(0);
        Bukkit.broadcastMessage(Prefix.SUBSERVER + NameUtils.getColoredName(player.getUniqueId(), player.getName()) + "§7 ist §cgestorben§7, weil er sich im Kampf ausgeloggt hat.");
    }

}
