package de.nebelniek;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "de.nebelniek")
public class BukkitSpringApplication {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(BukkitSpringApplication.class);
    }
}
