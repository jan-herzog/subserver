package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.nebelniek.ProxyConfiguration;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@CommandAlias("restartproxy")
@CommandPermission("proxy.restart")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RestartCommand extends BaseCommand {

    private final ProxyConfiguration configuration;

    @Default
    @Syntax("seconds")
    public void onDefault(ProxiedPlayer sender, @Single String seconds) {
        AtomicInteger secondsInt = new AtomicInteger(Integer.parseInt(seconds));
        sender.sendMessage(Prefix.PROXY + "§cRestart §7erfolgt in §a" + seconds + "§7 Sekunden.");
        ProxyServer.getInstance().broadcast(Prefix.PROXY + "Das komplette Netzwerk startet in §a" + secondsInt + "§7 neu...");
        ScheduledTask task;
        task = ProxyServer.getInstance().getScheduler().schedule(configuration.getCommandManager().getPlugin(), () -> {
            if(secondsInt.get() == 10 || secondsInt.get() == 5)
                ProxyServer.getInstance().broadcast(Prefix.PROXY + "Das komplette Netzwerk startet in §a" + secondsInt + "§7 neu...");
            if(secondsInt.get() == 0) {
                ProxyServer.getInstance().broadcast(Prefix.PROXY + "Das komplette Netzwerk startet neu...");
                ProxyServer.getInstance().stop();
            }
            secondsInt.getAndDecrement();
        }, 1, 1, TimeUnit.SECONDS);
    }

}
