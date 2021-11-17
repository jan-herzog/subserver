package de.nebelniek;

import de.nebelniek.application.ApplicationServiceMode;
import de.nebelniek.application.TabChatSpringApplication;
import de.nebelniek.configuration.TabChatConfiguration;
import de.nebelniek.utils.NameUtils;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TabChat extends JavaPlugin {

    @Getter
    private static AnnotationConfigApplicationContext context;

    @Override
    public void onEnable() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null)
            NameUtils.setLuckPerms(provider.getProvider());
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        context = new AnnotationConfigApplicationContext(TabChatSpringApplication.class);
        TabChatConfiguration tabChatConfiguration = context.getBean(TabChatConfiguration.class);
        if(context.getBean(ApplicationServiceMode.class).equals(ApplicationServiceMode.SUBSERVER)) {
            System.out.println("TabChat DEFAULT Mode wasn't enabled due to being started on a Subserver server instance.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        tabChatConfiguration.startBukkitPlugin(context, this);
        context.registerShutdownHook();
    }
}
