package de.nebelniek;

import lombok.SneakyThrows;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.TimeUnit;

public class ProxyPlugin extends Plugin {

    private AnnotationConfigApplicationContext context;

    @SneakyThrows
    @Override
    public void onEnable() {
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        context = new AnnotationConfigApplicationContext(ProxySpringApplication.class);
        System.out.println(context.getBeanDefinitionCount());
        for (String beanName : context.getBeanDefinitionNames()) {
            System.out.println(beanName);
        }
        ProxyConfiguration proxyConfiguration = context.getBean(ProxyConfiguration.class);
        proxyConfiguration.startProxyPlugin(context, this);
        context.registerShutdownHook();
        LuckPermsProvider.get();
        ProxyConfiguration.setLuckPerms(null);
    }

    @Override
    public void onDisable() {
        context.close();
        context = null;
    }
}
