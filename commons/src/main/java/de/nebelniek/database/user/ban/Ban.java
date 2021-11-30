package de.nebelniek.database.user.ban;

import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.ban.interfaces.IBan;
import de.nebelniek.database.user.ban.model.BanModel;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.Prefix;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class Ban implements IBan {

    @Getter
    @Setter
    private BanType banType;
    @Getter
    @Setter
    private String reason;
    @Getter
    @Setter
    private ICloudUser cloudUser;
    @Getter
    @Setter
    private Date endDate;

    @Getter
    private final BanModel model;

    private final CloudUserManagingService service;

    @SneakyThrows
    public Ban(CloudUserManagingService service, Long databaseId) {
        this.service = service;
        this.model = service.getDatabaseProvider().getBanDao().queryForId(databaseId);
    }


    @Override
    public CompletableFuture<Void> loadAsync() {
        return CompletableFuture.runAsync(this::load);
    }

    @SneakyThrows
    @Override
    public void load() {
        this.service.getDatabaseProvider().getBanDao().refresh(this.model);
        this.banType = BanType.valueOf(this.model.getType());
        this.endDate = this.model.getEndDate();
        this.reason = this.model.getReason();
        this.cloudUser = this.service.getCloudUsers().get(this.model.getCloudUserModel().getUuid());
    }

    @Override
    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(this::save);
    }

    @SneakyThrows
    @Override
    public void save() {
        this.model.setCloudUserModel(this.cloudUser.getModel());
        this.model.setType(banType.name());
        this.model.setEndDate(this.endDate);
        this.model.setReason(this.reason);
        this.service.getDatabaseProvider().getBanDao().update(this.model);
    }


    @Override
    public String toString(String banner) {
        return """
                %s  ➥ §eCloudUser §7➞ %s
                %s  ➥ §eTyp §7➞ %s
                %s  ➥ §eReason §7➞ %s
                %s  ➥ §eTeammitglied §7➞ %s
                """.formatted(Prefix.BAN, cloudUser.getLastUserName(), Prefix.BAN, banType.name(), Prefix.BAN, reason, Prefix.BAN, banner);
    }
}
