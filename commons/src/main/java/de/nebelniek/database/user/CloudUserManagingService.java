package de.nebelniek.database.user;

import de.nebelniek.database.DatabaseProvider;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.database.user.model.CloudUserModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CloudUserManagingService {

    @Getter
    private final DatabaseProvider databaseProvider;

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
        });
    }

    public CompletableFuture<? extends ICloudUser> loadUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> loadUserSync(uuid));
    }

    @SneakyThrows
    public ICloudUser loadUserSync(UUID uuid) {
        if (cloudUsers.containsKey(uuid)) {
            ICloudUser cloudUser = cloudUsers.get(uuid);
            cloudUser.load();
            return cloudUser;
        }
        CloudUserModel model = databaseProvider.getPlayerDao().queryBuilder().where().eq("uuid", uuid).queryForFirst();
        if (model == null)
            return null;
        CloudUser cloudUser = new CloudUser(CloudUserManagingService.this, model.getId());
        cloudUser.load();
        cloudUsers.put(uuid, cloudUser);
        return cloudUser;
    }

    public CompletableFuture<? extends ICloudUser> loadUserByTwitchId(String twitchId) {
        return CompletableFuture.supplyAsync(new Supplier<ICloudUser>() {
            @SneakyThrows
            @Override
            public ICloudUser get() {
                if (cloudUsers.entrySet().stream().anyMatch(entry -> entry.getValue().getTwitchId().equals(twitchId))) {
                    ICloudUser cloudUser = cloudUsers.entrySet().stream().filter(entry -> entry.getValue().getTwitchId().equals(twitchId)).findAny().get().getValue();
                    cloudUser.load();
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
        });
    }

}
