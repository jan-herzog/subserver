package de.nebelniek.database.guild;

import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.interfaces.IGuildSettings;
import de.nebelniek.database.guild.interfaces.IRegion;
import de.nebelniek.database.guild.model.GuildModel;
import de.nebelniek.database.interfaces.Loadable;
import de.nebelniek.database.interfaces.Saveable;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.database.user.model.CloudUserModel;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Builder
@AllArgsConstructor
public class Guild implements IGuild {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String color;

    @Getter
    @Setter
    private String prefix;

    @Getter
    @Setter
    private long balance;

    @Getter
    @Setter
    private IRegion region;

    @Getter
    @Setter
    private IGuildSettings settings;

    @Getter
    @Setter
    private List<ICloudUser> member;

    @Getter
    @Setter
    private List<IGuild> allies;

    @Getter
    private final GuildModel model;

    private final GuildManagingService service;

    @SneakyThrows
    public Guild(GuildManagingService service, Long databaseId) {
        this.service = service;
        this.model = service.getDatabaseProvider().getGuildDao().queryForId(databaseId);
    }


    public CompletableFuture<Void> loadAsync() {
        return CompletableFuture.runAsync(this::load);
    }

    @SneakyThrows
    @Override
    public void load() {
        this.service.getDatabaseProvider().getGuildDao().refresh(this.model);
        this.name = this.model.getName();
        this.color = this.model.getColor();
        this.prefix = this.model.getPrefix();
        this.balance = this.model.getBalance();
        if (this.region == null)
            this.region = this.service.modelToRegion(this.model.getRegionModel());
        this.region.load();
        if (this.settings == null)
            this.settings = this.service.modelToSettings(this.model.getSettingsModel());
        this.settings.load();
        this.member = new ArrayList<>();
        for (CloudUserModel cloudUserModel : this.model.getMember())
            this.member.add(service.getCloudUserManagingService().loadUserSync(cloudUserModel.getUuid()));
        this.allies = new ArrayList<>();
        for (String id : this.model.getAllies().split(";")) {
            IGuild guild = service.getGuildById(Long.parseLong(id));
            if (guild != null)
                this.allies.add(guild);
        }
    }

    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(this::save);
    }

    @SneakyThrows
    @Override
    public void save() {
        this.model.setName(this.name);
        this.model.setColor(this.color);
        this.model.setPrefix(this.prefix);
        this.model.setBalance(this.balance);
        this.model.setRegionModel(this.region.getModel());
        this.model.setSettingsModel(this.settings.getModel());
        this.model.getMember().clear();
        for (ICloudUser iCloudUser : this.member)
            this.model.getMember().add(iCloudUser.getModel());
        StringBuilder stringBuilder = new StringBuilder();
        for (IGuild iGuild : this.allies) {
            if (!stringBuilder.isEmpty())
                stringBuilder.append(";");
            stringBuilder.append(iGuild.getModel().getId());
        }
        this.model.setAllies(stringBuilder.toString());
        this.service.getDatabaseProvider().getGuildDao().update(this.model);
    }
}
