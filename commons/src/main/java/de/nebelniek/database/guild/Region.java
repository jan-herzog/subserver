package de.nebelniek.database.guild;

import de.nebelniek.database.guild.interfaces.IRegion;
import de.nebelniek.database.guild.model.RegionModel;
import de.nebelniek.database.guild.util.Direction;
import de.nebelniek.database.service.GuildManagingService;
import lombok.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.CompletableFuture;

@Builder
@AllArgsConstructor
public class Region implements IRegion {

    @Getter
    @Setter
    private String world;

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
        this.world = region.getWorld();
        this.aX = region.getAX();
        this.aZ = region.getAZ();
        this.bX = region.getBX();
        this.bZ = region.getBZ();
    }

    public Region(String world, double aX, double aZ, double bX, double bZ) {
        this.service = null;
        this.model = null;
        this.world = world;
        this.aX = aX;
        this.aZ = aZ;
        this.bX = bX;
        this.bZ = bZ;
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
        if (this.getWorld().equals(other.getWorld()))
            return getBounds(this).intersects(getBounds(other));
        return false;
    }

    public boolean doesCollide(String world, double aX, double aZ, double bX, double bZ) {
        if (this.getWorld().equals(world))
            return getBounds(this).intersects(getBounds(aX, aZ, bX, bZ));
        return false;
    }

    public boolean isIn(String world, double x, double z) {
        if (this.getWorld().equals(world))
            return x > aX && x < bX && z > aZ && z < bZ;
        return false;
    }

    private static Rectangle2D getBounds(double aX, double aZ, double bX, double bZ) {
        double x, w;
        if (aX < bX) {
            x = aX;
            w = bX - aX;
        } else {
            x = bX;
            w = aX - bX;
        }
        double y, h;
        if (aZ < bZ) {
            y = aZ;
            h = bZ - aZ;
        } else {
            y = bZ;
            h = aZ - bZ;
        }
        return new Rectangle2D.Double(x, y, w, h);
    }

    private static Rectangle2D getBounds(Region r) {
        double x, w;
        if (r.aX < r.bX) {
            x = r.aX;
            w = r.bX - r.aX;
        } else {
            x = r.bX;
            w = r.aX - r.bX;
        }
        double y, h;
        if (r.aZ < r.bZ) {
            y = r.aZ;
            h = r.bZ - r.aZ;
        } else {
            y = r.bZ;
            h = r.aZ - r.bZ;
        }
        return new Rectangle2D.Double(x, y, w, h);
    }

    public CompletableFuture<Void> loadAsync() {
        return CompletableFuture.runAsync(this::load);
    }

    @SneakyThrows
    @Override
    public void load() {
        this.service.getDatabaseProvider().getRegionDao().refresh(this.model);
        this.world = this.model.getWorld();
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
        this.model.setWorld(this.world);
        this.model.setAX(this.aX);
        this.model.setAZ(this.aZ);
        this.model.setBX(this.bX);
        this.model.setBZ(this.bZ);
        this.service.getDatabaseProvider().getRegionDao().update(this.model);
    }

    @Override
    public String toString() {
        return "Region{" +
                "aX=" + aX +
                ", aZ=" + aZ +
                ", bX=" + bX +
                ", bZ=" + bZ +
                '}';
    }
}
