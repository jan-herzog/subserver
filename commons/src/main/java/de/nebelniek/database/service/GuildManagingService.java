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
import de.nebelniek.database.user.model.CloudUserModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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

    public CompletableFuture<IGuild> createGuild(ICloudUser creator, String name) {
        return CompletableFuture.supplyAsync(new Supplier<IGuild>() {
            @SneakyThrows
            @Override
            public IGuild get() {
                GuildModel model = new GuildModel(name);
                databaseProvider.getGuildDao().create(model);
                databaseProvider.getGuildDao().refresh(model);
                databaseProvider.getGuildDao().assignEmptyForeignCollection(model, "member");
                databaseProvider.getGuildDao().assignEmptyForeignCollection(model, "allies");
                model.getMember().add(creator.getModel());
                Guild guild = new Guild(GuildManagingService.this, model.getId());
                guild.load();
                guilds.add(guild);
                return guild;
            }
        });
    }

    public IGuild getGuild(double x, double z) {
        for (IGuild guild : guilds) {
            IRegion region = guild.getRegion();
            if (x > region.getBX() && x < region.getAX() && z > region.getAZ() && z < region.getBZ())
                return guild;
        }
        return null;
    }

    public CompletableFuture<IRegion> createRegion(double aX, double aZ, double bX, double bZ) {
        return CompletableFuture.supplyAsync(new Supplier<IRegion>() {
            @SneakyThrows
            @Override
            public IRegion get() {
                RegionModel model = new RegionModel(aX, aZ, bX, bZ);
                databaseProvider.getRegionDao().create(model);
                databaseProvider.getRegionDao().refresh(model);
                Region region = new Region(GuildManagingService.this, model);
                region.load();
                return region;
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
