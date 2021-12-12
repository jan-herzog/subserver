package de.nebelniek.services.ban;

import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.ban.BanType;
import de.nebelniek.database.user.ban.interfaces.IBan;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BanService implements Listener {

    private final CloudUserManagingService cloudUserManagingService;

    public CompletableFuture<? extends IBan> createBan(ICloudUser cloudUser, BanType banType, String duration, String reason) {
        Date date = date(duration);
        return cloudUserManagingService.createBan(cloudUser, banType, reason, date);
    }

    public boolean isMute(ICloudUser cloudUser) {
        if (cloudUser.getBan() == null)
            return false;
        return cloudUser.getBan().getBanType() == BanType.MUTE;
    }

    @SneakyThrows
    public boolean unban(ICloudUser cloudUser) {
        if (cloudUser.getBan() == null)
            return false;
        cloudUserManagingService.getDatabaseProvider().getBanDao().delete(cloudUser.getBan().getModel());
        cloudUser.setBan(null);
        cloudUser.saveAsync();
        return true;
    }

    public Date date(String duration) {
        TimeUnitIndicator timeUnitIndicator = TimeUnitIndicator.get(duration);
        if (timeUnitIndicator == null)
            return null;
        int durationTime = Integer.parseInt(duration.replace(timeUnitIndicator.getIndicator(), ""));
        switch (timeUnitIndicator) {
            case DAYS -> {
                return new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(durationTime));
            }
            case HOURS -> {
                return new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(durationTime));
            }
            case MINUTES -> {
                return new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(durationTime));
            }
        }
        return null;
    }

    public void notify(String message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!player.hasPermission("proxy.ban.broadcasts"))
                continue;
            player.sendMessage(message);
        }
    }

    @SneakyThrows
    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        if (event.getConnection().getName().equalsIgnoreCase("Kexesser"))
            return;
        ICloudUser cloudUser = cloudUserManagingService.loadUserByNameSync(event.getConnection().getName());
        if (cloudUser == null)
            return;
        if (cloudUser.getBan() == null)
            return;
        System.out.println(cloudUser.getBan());
        System.out.println(cloudUser.getBan().getBanType());
        if (cloudUser.getBan().getBanType().equals(BanType.PROXY_BAN)) {
            event.setCancelReason(BanScreen.timeLeft(cloudUser.getBan().getEndDate(), cloudUser.getBan().getReason()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        Connection sender = event.getSender();
        if (!(sender instanceof ProxiedPlayer))
            return;
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (event.getMessage().charAt(0) == '/')
            return;
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(player.getUniqueId());
        if (cloudUser.getBan() == null)
            return;
        if (cloudUser.getBan().getBanType().equals(BanType.MUTE)) {
            player.sendMessage(Prefix.BAN + "Du bist §cgemutet§7!");
            player.sendMessage("""
                    %s  ➥ §eGrund §7➞ %s
                    %s  ➥ §eZeit §7➞ %s
                    """.formatted(Prefix.BAN, cloudUser.getBan().getReason() == null ? "Kein Grund angegeben!" : cloudUser.getBan().getReason(), Prefix.BAN, cloudUser.getBan().getEndDate() == null ? "§cPermanent" : BanScreen.format.format(cloudUser.getBan().getEndDate()))
            );
            event.setCancelled(true);
        }
    }

}
