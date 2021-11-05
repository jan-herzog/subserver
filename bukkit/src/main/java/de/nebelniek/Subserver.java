package de.nebelniek;

import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Subserver extends JavaPlugin {

    private AnnotationConfigApplicationContext context;

    @Override
    public void onEnable() {
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        context = new AnnotationConfigApplicationContext(BukkitSpringApplication.class);
        System.out.println(context.getBeanDefinitionCount());
        for (String beanName : context.getBeanDefinitionNames()) {
            System.out.println(beanName);
        }
        context.registerShutdownHook();
    }

    //TODO: GuildContentService -> Funktionen und shit von gilden für gui und commands
    //TODO: ChannelPointService -> Channel Points in coins einlösen
    //TODO: discord-bot? -> Automatisches erstellen von channeln

    //TODO: RegionEnterEvent -> Show property owner
    //TODO: Scoreboard -> Gilde, Gildenrang, Property, Coins
}
