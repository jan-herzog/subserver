package de.nebelniek;

import de.nebelniek.configuration.BukkitConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Subserver extends JavaPlugin {

    private AnnotationConfigApplicationContext context;

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

    //TODO: Guild Leave last one -> delete guild
    //TODO: Guild Bank Command to transfer money
    //TODO: Guild Home

    //TODO: Guild Invite
    //TODO: Guild ally
    //TODO: discord-bot? -> Automatisches erstellen von channeln

}
