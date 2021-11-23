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
import java.util.Objects;
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

    public CompletableFuture<Void> loadGuilds() {
        return CompletableFuture.runAsync(() -> {
            try {
                for (GuildModel model : databaseProvider.getGuildDao().queryForAll()) {
                    if (guilds.stream().anyMatch(g -> g.getModel().getId() == model.getId())) {
                        guilds.stream().filter(g -> g.getModel().getId() == model.getId()).findAny().get().load();
                        continue;
                    }
                    Guild guild = new Guild(this, model.getId());
                    guild.load();
                    guilds.add(guild);
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public CompletableFuture<IGuild> createGuild(ICloudUser creator, String name) {
        return CompletableFuture.supplyAsync(new Supplier<IGuild>() {
            @SneakyThrows
            @Override
            public IGuild get() {
                GuildModel model = new GuildModel(name);
                model.setMember(String.valueOf(creator.getModel().getId()));
                databaseProvider.getGuildDao().create(model);
                databaseProvider.getGuildDao().refresh(model);
                Guild guild = new Guild(GuildManagingService.this, model.getId());
                guild.load();
                guilds.add(guild);
                return guild;
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @SneakyThrows
    public void deleteGuild(IGuild guild) {
        guilds.remove(guild);
        databaseProvider.getGuildSettingsDao().delete(guild.getModel().getSettingsModel());
        databaseProvider.getGuildDao().delete(guild.getModel());
    }


    public IGuild getGuildAt(double x, double z) {
        for (IGuild guild : guilds) {
            IRegion region = guild.getRegion();
            if (region != null) {
                if (x > region.getAX() && x < region.getBX() && z > region.getAZ() && z < region.getBZ())
                    return guild;
            }
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
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @SneakyThrows
    public IGuild getGuildByUser(ICloudUser cloudUser) {
        return guilds.stream().filter(iGuild -> iGuild.getMember().stream().anyMatch(member -> member.getUuid().equals(cloudUser.getUuid()))).findAny().orElse(null);
    }

    @SneakyThrows
    public IGuild getGuildByUserCheck(ICloudUser cloudUser) {
        IGuild result = guilds.stream().filter(iGuild -> iGuild.getMember().stream().anyMatch(member -> member.getUuid().equals(cloudUser.getUuid()))).findAny().orElse(null);
        if (result == null || databaseProvider.getGuildDao().idExists(result.getModel().getId()))
            return result;
        return null;
    }

    public IGuild getGuildById(long id) {
        return guilds.stream().filter(iGuild -> iGuild.getModel().getId() == id).findAny().orElse(null);
    }

    public IGuild getGuildByName(String name) {
        return guilds.stream().filter(iGuild -> iGuild.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public IRegion modelToRegion(RegionModel model) {
        return new Region(this, model);
    }

    public IGuildSettings modelToSettings(GuildSettingsModel model) {
        return new GuildSettings(this, model);
    }

}
