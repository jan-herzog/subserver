package de.nebelniek.database.service;

import de.nebelniek.database.DatabaseProvider;
import de.nebelniek.database.guild.Guild;
import de.nebelniek.database.guild.GuildSettings;
import de.nebelniek.database.guild.Region;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.interfaces.IGuildSettings;
import de.nebelniek.database.guild.interfaces.IRegion;
import de.nebelniek.database.guild.model.GuildModel;
import de.nebelniek.database.guild.model.GuildSettingsModel;
import de.nebelniek.database.guild.model.RegionModel;
import de.nebelniek.database.user.CloudUser;
import de.nebelniek.database.user.interfaces.ICloudUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class GuildManagingService {

    @Getter
    private final CloudUserManagingService cloudUserManagingService;

    @Getter
    private final DatabaseProvider databaseProvider;

    @Autowired
    public GuildManagingService(@Lazy CloudUserManagingService cloudUserManagingService, DatabaseProvider databaseProvider) {
        this.cloudUserManagingService = cloudUserManagingService;
        this.databaseProvider = databaseProvider;
    }

    @Getter
    private final List<IGuild> guilds = new ArrayList<>();

    public void loadGuilds() {
        CompletableFuture.runAsync(() -> {
            try {
                for (GuildModel model : databaseProvider.getGuildDao().queryForAll()) {
                    Guild guild = new Guild(this, model.getId());
                    guild.load();
                    guilds.add(guild);
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public IGuild getGuildByUser(ICloudUser cloudUser) {
        return guilds.stream().filter(iGuild -> iGuild.getMember().stream().anyMatch(member -> member.getUuid().equals(cloudUser.getUuid()))).findAny().orElse(null);
    }

    public IGuild getGuildById(long id) {
        return guilds.stream().filter(iGuild -> iGuild.getModel().getId() == id).findAny().orElse(null);
    }

    public IRegion modelToRegion(RegionModel model) {
        return new Region(this, model);
    }

    public IGuildSettings modelToSettings(GuildSettingsModel model) {
        return new GuildSettings(this, model);
    }

}
