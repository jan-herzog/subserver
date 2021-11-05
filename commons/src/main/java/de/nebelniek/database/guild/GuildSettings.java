package de.nebelniek.database.guild;

import com.j256.ormlite.field.DatabaseField;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.interfaces.IGuildSettings;
import de.nebelniek.database.guild.model.GuildModel;
import de.nebelniek.database.guild.model.GuildSettingsModel;
import de.nebelniek.database.guild.util.GuildRole;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.database.user.model.CloudUserModel;
import lombok.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Builder
@AllArgsConstructor
public class GuildSettings implements IGuildSettings {

    @Getter
    @Setter
    private GuildRole manageBankAccountRole;

    @Getter
    @Setter
    private GuildRole manageRegionRole;

    @Getter
    @Setter
    private GuildRole manageMembersRole;

    @Getter
    private final GuildSettingsModel model;

    private final GuildManagingService service;

    @SneakyThrows
    public GuildSettings(GuildManagingService service, GuildSettingsModel model) {
        this.service = service;
        this.model = model;
    }

    public CompletableFuture<Void> loadAsync() {
        return CompletableFuture.runAsync(this::load);
    }

    @SneakyThrows
    @Override
    public void load() {
        this.service.getDatabaseProvider().getGuildSettingsDao().refresh(this.model);
        this.manageMembersRole = GuildRole.valueOf(this.model.getManageMembersRole());
        this.manageRegionRole = GuildRole.valueOf(this.model.getManageRegionRole());
        this.manageBankAccountRole = GuildRole.valueOf(this.model.getManageBankAccountRole());
    }
    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(this::save);
    }

    @SneakyThrows
    @Override
    public void save() {
        this.model.setManageBankAccountRole(this.manageBankAccountRole.name());
        this.model.setManageMembersRole(this.manageMembersRole.name());
        this.model.setManageRegionRole(this.manageRegionRole.name());
        this.service.getDatabaseProvider().getGuildSettingsDao().update(this.model);
    }

}
