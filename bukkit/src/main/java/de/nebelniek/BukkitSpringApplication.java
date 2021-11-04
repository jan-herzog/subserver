package de.nebelniek;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "de.nebelniek")
@EnableJpaRepositories(basePackages = "de.nebelniek.database.user")
public class BukkitSpringApplication {

}
