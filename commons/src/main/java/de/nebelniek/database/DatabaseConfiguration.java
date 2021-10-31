package de.nebelniek.database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("out");
        dataSource.setPassword("polen1hzg");
        dataSource.setUrl("jdbc:mariadb://notecho.de:3306/backend?createDatabaseIfNotExist=true");
        return dataSource;
    }

}
