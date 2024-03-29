package de.nebelniek.database.user;

import de.nebelniek.database.guild.Guild;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.util.GuildRole;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.ban.Ban;
import de.nebelniek.database.user.ban.interfaces.IBan;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.database.user.model.CloudUserModel;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private String discordId;
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
    @Setter
    private IBan ban;
    @Getter
    @Setter
    private List<ICloudUser> ignored;
    @Getter
    @Setter
    private String textureHash;

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
        this.discordId = this.model.getDiscordId();
        this.subbed = this.model.isSubbed();
        this.textureHash = this.model.getTextureHash();
        this.coins = this.model.getCoins();
        this.guildRole = this.model.getGuildRole() != null ? GuildRole.valueOf(this.model.getGuildRole()) : null;
        this.guild = service.getGuildManagingService().getGuildByUser(this);
        if (this.model.getBan() == null)
            return;
        this.ignored = new ArrayList<>();
        for (String s : this.model.getIgnored().split(";"))
            this.ignored.add(this.service.loadUserByIdSync(Long.parseLong(s)));
        this.ban = new Ban(this.service, this.model.getBan().getId());
        this.ban.loadAsync();
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
        this.model.setTextureHash(this.textureHash);
        this.model.setDiscordId(this.discordId);
        this.model.setTwitchId(this.twitchId);
        this.model.setSubbed(this.subbed);
        this.model.setCoins(this.coins);
        this.model.setGuildRole(this.guildRole == null ? null : this.guildRole.name());
        this.model.setGuildModel(this.guild == null ? null : this.guild.getModel());
        this.model.setBan(this.ban == null ? null : this.ban.getModel());
        if (this.ban != null)
            this.ban.saveAsync();
        StringBuilder stringBuilder = new StringBuilder();
        for (ICloudUser iCloudUser : this.ignored) {
            if (!stringBuilder.isEmpty())
                stringBuilder.append(";");
            stringBuilder.append(iCloudUser.getModel().getId());
        }
        this.model.setIgnored(stringBuilder.toString());
        this.service.getDatabaseProvider().getPlayerDao().update(this.model);
    }
}
