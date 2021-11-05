package de.nebelniek.database.guild;

import de.nebelniek.database.guild.interfaces.IRegion;
import de.nebelniek.database.guild.model.RegionModel;
import de.nebelniek.database.service.GuildManagingService;
import lombok.*;

import java.util.concurrent.CompletableFuture;

@Builder
@AllArgsConstructor
public class Region implements IRegion {

    @Getter
    @Setter
    private double centerX;

    @Getter
    @Setter
    private double centerY;

    @Getter
    @Setter
    private double centerZ;

    @Getter
    @Setter
    private int range;

    @Getter
    private final RegionModel model;

    private final GuildManagingService service;

    @SneakyThrows
    public Region(GuildManagingService service, RegionModel model) {
        this.service = service;
        this.model = model;
    }

    public CompletableFuture<Void> loadAsync() {
        return CompletableFuture.runAsync(this::load);
    }

    @SneakyThrows
    @Override
    public void load() {
        this.service.getDatabaseProvider().getRegionDao().refresh(this.model);
        this.centerX =  this.model.getCenterX();
        this.centerY = this.model.getCenterY();
        this.centerZ = this.model.getCenterZ();
        this.range = this.model.getRange();
    }
    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(this::save);
    }

    @SneakyThrows
    @Override
    public void save() {
        this.model.setCenterX(this.centerX);
        this.model.setCenterY(this.centerY);
        this.model.setCenterZ(this.centerZ);
        this.model.setRange(this.range);
        this.service.getDatabaseProvider().getRegionDao().update(this.model);
    }
}
