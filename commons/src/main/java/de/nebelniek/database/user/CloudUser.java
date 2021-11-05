package de.nebelniek.database.user;

import de.nebelniek.database.guild.Guild;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.util.GuildRole;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.database.user.model.CloudUserModel;
import lombok.*;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Builder
@AllArgsConstructor
public class CloudUser implements ICloudUser {

    @Getter
    @Setter
    private UUID uuid;
    @Getter
    @Setter
    private String lastUserName;
    @Getter
    @Setter
    private long coins;
    @Getter
    @Setter
    private Date lastLogin;
    @Getter
    @Setter
    private String twitchId;
    @Getter
    @Setter
    private boolean subbed;
    @Getter
    @Setter
    private GuildRole guildRole;
    @Getter
    @Setter
    private IGuild guild;

    @Getter
    private final CloudUserModel model;

    private final CloudUserManagingService service;

    @SneakyThrows
    public CloudUser(CloudUserManagingService service, Long databaseId) {
        this.service = service;
        this.model = service.getDatabaseProvider().getPlayerDao().queryForId(databaseId);
    }


    public CompletableFuture<Void> loadAsync() {
        return CompletableFuture.runAsync(this::load);
    }

    @SneakyThrows
    @Override
    public void load() {
        this.service.getDatabaseProvider().getPlayerDao().refresh(this.model);
        this.uuid = this.model.getUuid();
        this.lastLogin = this.model.getLastLogin();
        this.lastUserName = this.model.getLastUserName();
        this.twitchId = this.model.getTwitchId();
        this.subbed = this.model.isSubbed();
        this.coins = this.model.getCoins();
        this.guildRole = this.guildRole != null ? GuildRole.valueOf(this.model.getGuildRole()) : null;
        this.guild = service.getGuildManagingService().getGuildByUser(this);
    }


    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(this::save);
    }

    @SneakyThrows
    @Override
    public void save() {
        this.model.setUuid(this.uuid);
        this.model.setLastLogin(this.lastLogin);
        this.model.setLastUserName(this.lastUserName);
        this.model.setTwitchId(this.twitchId);
        this.model.setSubbed(this.subbed);
        this.model.setCoins(this.coins);
        this.model.setGuildRole(this.guildRole == null ? null : this.guildRole.name());
        this.model.setGuildModel(this.guild == null ? null : this.guild.getModel());
        this.service.getDatabaseProvider().getPlayerDao().update(this.model);
    }
}
