package de.nebelniek.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.nebelniek.database.user.model.CloudUserModel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class DatabaseProvider {

    @Getter
    private final Dao<CloudUserModel, Long> playerDao;

    @SneakyThrows
    public DatabaseProvider() {
        ConnectionSource connectionSource = new JdbcPooledConnectionSource("jdbc:mariadb://alpha.server.notecho.de:3306/backend", "out", "polen1hzg");
        this.playerDao = DaoManager.createDao(connectionSource, CloudUserModel.class);
        TableUtils.createTableIfNotExists(connectionSource, CloudUserModel.class);
    }

}
