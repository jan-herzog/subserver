package de.nebelniek.database.guild;

import de.nebelniek.database.guild.interfaces.IRegion;
import de.nebelniek.database.guild.model.RegionModel;
import de.nebelniek.database.guild.util.Direction;
import de.nebelniek.database.service.GuildManagingService;
import lombok.*;

import java.util.concurrent.CompletableFuture;

@Builder
@AllArgsConstructor
public class Region implements IRegion {

    @Getter
    @Setter
    private double aX;

    @Getter
    @Setter
    private double aZ;

    @Getter
    @Setter
    private double bX;

    @Getter
    @Setter
    private double bZ;

    @Getter
    private final RegionModel model;

    private final GuildManagingService service;

    @SneakyThrows
    public Region(GuildManagingService service, RegionModel model) {
        this.service = service;
        this.model = model;
    }

    public Region(IRegion region) {
        this.service = null;
        this.model = null;
        this.aX = region.getAX();
        this.aZ = region.getAZ();
        this.bX = region.getBX();
        this.bZ = region.getBZ();
    }

    public void expand(int blocks, Direction direction) {
        switch (direction) {
            case NORTH -> this.aZ -= blocks;
            case EAST -> this.bX += blocks;
            case SOUTH -> this.bZ += blocks;
            case WEST -> this.aX -= blocks;
        }
    }

    public boolean doesCollide(Region other) {
        if (aX == bX || aZ == bZ || other.aX == other.bX || other.aZ == other.bZ)
            return false;
        if (aX >= other.bX || other.aX >= bX)
            return false;
        if (bZ >= other.aZ || other.bZ >= aZ)
            return false;
        return true;
    }

    public CompletableFuture<Void> loadAsync() {
        return CompletableFuture.runAsync(this::load);
    }

    @SneakyThrows
    @Override
    public void load() {
        this.service.getDatabaseProvider().getRegionDao().refresh(this.model);
        this.aX = this.model.getAX();
        this.aZ = this.model.getAZ();
        this.bX = this.model.getBX();
        this.bZ = this.model.getBZ();
    }

    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(this::save);
    }

    @SneakyThrows
    @Override
    public void save() {
        this.model.setAX(this.aX);
        this.model.setAZ(this.aZ);
        this.model.setBX(this.bX);
        this.model.setBZ(this.bZ);
        this.service.getDatabaseProvider().getRegionDao().update(this.model);
    }
}
