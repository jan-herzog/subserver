package de.nebelniek.database.guild.interfaces;

import de.nebelniek.database.guild.Region;
import de.nebelniek.database.guild.model.RegionModel;
import de.nebelniek.database.guild.util.Direction;
import de.nebelniek.database.interfaces.Loadable;
import de.nebelniek.database.interfaces.Saveable;

public interface IRegion extends Loadable, Saveable {


    String getWorld();

    double getAX();

    double getAZ();

    double getBX();

    double getBZ();

    void setWorld(String world);

    void setAX(double aX);

    void setAZ(double aZ);

    void setBX(double bX);

    void setBZ(double bZ);

    void expand(int blocks, Direction direction);

    boolean doesCollide(Region other);

    boolean doesCollide(String world, double aX, double aZ, double bX, double bZ);

    RegionModel getModel();

}
