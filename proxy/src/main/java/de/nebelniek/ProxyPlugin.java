package de.nebelniek;

import lombok.SneakyThrows;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.plugin.Plugin;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
        ProxyConfiguration.setLuckPerms(createLuckPerms());
    }

    @Override
    public void onDisable() {
        context.close();
        context = null;
    }

    public LuckPerms createLuckPerms() {
        return LuckPermsProvider.get();
    }
}
