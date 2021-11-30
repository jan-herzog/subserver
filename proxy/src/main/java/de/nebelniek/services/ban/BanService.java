package de.nebelniek.services.ban;

import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.ban.BanType;
import de.nebelniek.database.user.ban.interfaces.IBan;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BanService {

    private final CloudUserManagingService cloudUserManagingService;


    public CompletableFuture<? extends IBan> createBan(ICloudUser cloudUser, BanType banType, String duration, String reason) {
        Date date = date(duration);
        return cloudUserManagingService.createBan(cloudUser, banType, reason, date);
    }

    public boolean unban(ICloudUser cloudUser) {
        if(cloudUser.getBan() == null)
            return false;
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

}
