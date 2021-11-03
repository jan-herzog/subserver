package de.nebelniek.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan({ "de.nebelniek" })
@EnableJpaRepositories("de.nebelniek.database.user")
public class BukkitSpringApplication {

}
