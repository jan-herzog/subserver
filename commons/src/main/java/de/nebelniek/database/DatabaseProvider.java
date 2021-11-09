package de.nebelniek.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.nebelniek.database.guild.model.GuildModel;
import de.nebelniek.database.guild.model.GuildSettingsModel;
import de.nebelniek.database.guild.model.RegionModel;
import de.nebelniek.database.user.model.CloudUserModel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class DatabaseProvider {

    @Getter
    private final Dao<CloudUserModel, Long> playerDao;

    @Getter
    private final Dao<GuildModel, Long> guildDao;
    @Getter
    private final Dao<GuildSettingsModel, Long> guildSettingsDao;
    @Getter
    private final Dao<RegionModel, Long> regionDao;

    @SneakyThrows
    public DatabaseProvider() {
        ConnectionSource connectionSource = new JdbcPooledConnectionSource("jdbc:mariadb://alpha.server.notecho.de:3306/backend?autoReconnect=true", "out", "polen1hzg");
        this.playerDao = DaoManager.createDao(connectionSource, CloudUserModel.class);
        TableUtils.createTableIfNotExists(connectionSource, CloudUserModel.class);
        this.guildSettingsDao = DaoManager.createDao(connectionSource, GuildSettingsModel.class);
        TableUtils.createTableIfNotExists(connectionSource, GuildSettingsModel.class);
        this.regionDao = DaoManager.createDao(connectionSource, RegionModel.class);
        TableUtils.createTableIfNotExists(connectionSource, RegionModel.class);
        this.guildDao = DaoManager.createDao(connectionSource, GuildModel.class);
        TableUtils.createTableIfNotExists(connectionSource, GuildModel.class);
    }

}
