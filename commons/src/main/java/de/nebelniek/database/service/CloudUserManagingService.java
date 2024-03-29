package de.nebelniek.database.service;

import de.nebelniek.database.DatabaseProvider;
import de.nebelniek.database.guild.util.GuildRole;
import de.nebelniek.database.user.CloudUser;
import de.nebelniek.database.user.ban.Ban;
import de.nebelniek.database.user.ban.BanType;
import de.nebelniek.database.user.ban.interfaces.IBan;
import de.nebelniek.database.user.ban.model.BanModel;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.database.user.model.CloudUserModel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class CloudUserManagingService {

    @Getter
    private final GuildManagingService guildManagingService;

    @Getter
    private final DatabaseProvider databaseProvider;

    @Autowired
    public CloudUserManagingService(@Lazy GuildManagingService guildManagingService, DatabaseProvider databaseProvider) {
        this.guildManagingService = guildManagingService;
        this.databaseProvider = databaseProvider;
    }

    @Getter
    private final Map<UUID, ICloudUser> cloudUsers = new HashMap<>();

    public CompletableFuture<? extends ICloudUser> createUserIfNotExists(UUID uuid, String name) {
        return CompletableFuture.supplyAsync(new Supplier<ICloudUser>() {
            @SneakyThrows
            @Override
            public ICloudUser get() {
                if (cloudUsers.containsKey(uuid)) {
                    ICloudUser cloudUser = cloudUsers.get(uuid);
                    cloudUser.load();
                    return cloudUser;
                }
                CloudUserModel model = databaseProvider.getPlayerDao().queryBuilder().where().eq("uuid", uuid).queryForFirst();
                if (model == null) {
                    model = new CloudUserModel(uuid, name);
                    databaseProvider.getPlayerDao().create(model);
                    databaseProvider.getPlayerDao().refresh(model);
                }
                CloudUser cloudUser = new CloudUser(CloudUserManagingService.this, model.getId());
                cloudUser.load();
                cloudUsers.put(uuid, cloudUser);
                return cloudUser;
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public CompletableFuture<? extends ICloudUser> loadUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> loadUserSync(uuid)).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public CompletableFuture<? extends ICloudUser> getUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (cloudUsers.containsKey(uuid)) {
                ICloudUser cloudUser = cloudUsers.get(uuid);
                cloudUser.load();
                return cloudUser;
            }
            return loadUserSync(uuid);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @SneakyThrows
    public ICloudUser loadUserSync(UUID uuid) {
        if (cloudUsers.containsKey(uuid)) {
            ICloudUser cloudUser = cloudUsers.get(uuid);
            cloudUser.load();
            if(cloudUser.getGuild() != null && cloudUser.getGuildRole() == null) {
                cloudUser.setGuildRole(GuildRole.LEADER);
                cloudUser.saveAsync();
            }
            return cloudUser;
        }
        CloudUserModel model = databaseProvider.getPlayerDao().queryBuilder().where().eq("uuid", uuid).queryForFirst();
        if (model == null)
            return null;
        CloudUser cloudUser = new CloudUser(CloudUserManagingService.this, model.getId());
        cloudUser.load();
        if(cloudUser.getGuild() != null && cloudUser.getGuildRole() == null) {
            cloudUser.setGuildRole(GuildRole.LEADER);
            cloudUser.saveAsync();
        }
        cloudUsers.put(uuid, cloudUser);
        return cloudUser;
    }

    public CompletableFuture<? extends ICloudUser> loadUserByTwitchId(String twitchId) {
        return CompletableFuture.supplyAsync(() -> loadUserByTwitchIdSync(twitchId)).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @SneakyThrows
    public ICloudUser loadUserByTwitchIdSync(String twitchId) {
        if (cloudUsers.entrySet().stream().anyMatch(entry -> entry.getValue().getTwitchId() != null && entry.getValue().getTwitchId().equals(twitchId))) {
            ICloudUser cloudUser = cloudUsers.entrySet().stream().filter(entry -> entry.getValue().getTwitchId() != null && entry.getValue().getTwitchId().equals(twitchId)).findAny().get().getValue();
            cloudUser.load();
            if (cloudUser.getTwitchId() != null && cloudUser.getTwitchId().equalsIgnoreCase(twitchId))
                return cloudUser;
        }
        CloudUserModel model = databaseProvider.getPlayerDao().queryBuilder().where().eq("twitchId", twitchId).queryForFirst();
        if (model == null)
            return null;
        CloudUser cloudUser = new CloudUser(CloudUserManagingService.this, model.getId());
        cloudUser.load();
        cloudUsers.put(cloudUser.getUuid(), cloudUser);
        return cloudUser;
    }

    public CompletableFuture<? extends ICloudUser> loadUserByDiscordId(String discordId) {
        return CompletableFuture.supplyAsync(new Supplier<ICloudUser>() {
            @SneakyThrows
            @Override
            public ICloudUser get() {
                if (cloudUsers.entrySet().stream().anyMatch(entry -> entry.getValue().getDiscordId() != null && entry.getValue().getDiscordId().equals(discordId))) {
                    ICloudUser cloudUser = cloudUsers.entrySet().stream().filter(entry -> entry.getValue().getDiscordId() != null && entry.getValue().getDiscordId().equals(discordId)).findAny().get().getValue();
                    cloudUser.load();
                    if (cloudUser.getDiscordId().equals(discordId))
                        return cloudUser;
                }
                CloudUserModel model = databaseProvider.getPlayerDao().queryBuilder().where().eq("discordId", discordId).queryForFirst();
                if (model == null)
                    return null;
                CloudUser cloudUser = new CloudUser(CloudUserManagingService.this, model.getId());
                cloudUser.load();
                cloudUsers.put(cloudUser.getUuid(), cloudUser);
                return cloudUser;
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public CompletableFuture<? extends ICloudUser> loadUserByName(String name) {
        return CompletableFuture.supplyAsync(() -> loadUserByNameSync(name)).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @SneakyThrows
    public ICloudUser loadUserByNameSync(String name) {
        if (cloudUsers.entrySet().stream().anyMatch(entry -> entry.getValue().getLastUserName().equals(name))) {
            ICloudUser cloudUser = cloudUsers.entrySet().stream().filter(entry -> entry.getValue().getLastUserName().equals(name)).findAny().get().getValue();
            cloudUser.load();
            return cloudUser;
        }
        CloudUserModel model = databaseProvider.getPlayerDao().queryBuilder().where().eq("lastUserName", name).queryForFirst();
        if (model == null)
            return null;
        CloudUser cloudUser = new CloudUser(CloudUserManagingService.this, model.getId());
        cloudUser.load();
        cloudUsers.put(cloudUser.getUuid(), cloudUser);
        return cloudUser;
    }

    @SneakyThrows
    public ICloudUser loadUserByIdSync(long id) {
        if (cloudUsers.entrySet().stream().anyMatch(entry -> entry.getValue().getModel().getId() == id)) {
            ICloudUser cloudUser = cloudUsers.entrySet().stream().filter(entry -> entry.getValue().getModel().getId() == id).findAny().get().getValue();
            cloudUser.load();
            return cloudUser;
        }
        CloudUserModel model = databaseProvider.getPlayerDao().queryBuilder().where().eq("id", id).queryForFirst();
        if (model == null)
            return null;
        CloudUser cloudUser = new CloudUser(CloudUserManagingService.this, model.getId());
        cloudUser.load();
        cloudUsers.put(cloudUser.getUuid(), cloudUser);
        return cloudUser;
    }


    @SneakyThrows
    public IBan createBanSync(ICloudUser cloudUser, BanType banType, String reason, Date endDate) {
        if (cloudUser.getBan() != null) {
            IBan ban = cloudUser.getBan();
            ban.setBanType(banType);
            ban.setEndDate(endDate);
            ban.saveAsync();
            return ban;
        }
        BanModel model = new BanModel(cloudUser.getModel(), banType.name(), reason, endDate);
        this.databaseProvider.getBanDao().create(model);
        Ban ban = new Ban(this, model.getId());
        ban.loadAsync();
        cloudUser.setBan(ban);
        cloudUser.saveAsync();
        return ban;
    }

    public CompletableFuture<? extends IBan> createBan(ICloudUser cloudUser, BanType banType, String reason, Date endDate) {
        return CompletableFuture.supplyAsync(() -> createBanSync(cloudUser, banType, reason, endDate)).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }


}
