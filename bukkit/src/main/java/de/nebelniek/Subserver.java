package de.nebelniek;

import de.nebelniek.configuration.BukkitConfiguration;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Subserver extends JavaPlugin {

    @Getter
    private static AnnotationConfigApplicationContext context;

    @Override
    public void onEnable() {
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        context = new AnnotationConfigApplicationContext(BukkitSpringApplication.class);
        BukkitConfiguration bukkitConfiguration = context.getBean(BukkitConfiguration.class);
        bukkitConfiguration.startBukkitPlugin(context, this);
        context.registerShutdownHook();
    }

    //TODO: GuildContentService -> Funktionen und shit von gilden für gui und commands
    //TODO: ChannelPointService -> Channel Points in coins einlösen
    //TODO: RegionEnterEvent -> Show property owner
    //TODO: Scoreboard -> Gilde, Gildenrang, Property, Coins
    //TODO: Guild Bank Command to transfer money
    //TODO: Guild Home
    //TODO: Guild Leave last one -> delete guild
    //TODO: Guild Invite
    //TODO: Guild Region protection
    //TODO: Guild Member Kick
    //TODO: Guild Member Promote/Demote
    //TODO: Guild ally

    //TODO: Join Message to Guild
    //TODO: discord-bot? -> Automatisches erstellen von channeln

}
