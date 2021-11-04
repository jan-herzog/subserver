package de.nebelniek;

import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Subserver extends JavaPlugin {

    private AnnotationConfigApplicationContext context;

    @SneakyThrows
    @Override
    public void onEnable() {
        Thread.currentThread().setContextClassLoader(getClassLoader());
        context = new AnnotationConfigApplicationContext(BukkitSpringApplication.class);
        System.out.println(context.getBeanDefinitionCount());
        for (String beanName : context.getBeanDefinitionNames()) {
            System.out.println(beanName);
        }
        BukkitConfiguration bukkitConfiguration = context.getBean(BukkitConfiguration.class);
        bukkitConfiguration.startMinecraftPlugin(context, this);
        context.registerShutdownHook();
    }

    @Override
    public void onDisable() {
        context.close();
        context = null;
    }
}
