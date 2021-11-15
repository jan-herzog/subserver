package de.nebelniek;

import de.nebelniek.application.TabChatSpringApplication;
import de.nebelniek.configuration.TabChatConfiguration;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TabChat extends JavaPlugin {

    @Getter
    private static AnnotationConfigApplicationContext context;

    @Override
    public void onEnable() {
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        context = new AnnotationConfigApplicationContext(TabChatSpringApplication.class);
        TabChatConfiguration tabChatConfiguration = context.getBean(TabChatConfiguration.class);
        tabChatConfiguration.startBukkitPlugin(context, this);
        context.registerShutdownHook();
    }
}
