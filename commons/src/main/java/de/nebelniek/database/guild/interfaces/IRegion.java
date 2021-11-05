package de.nebelniek.database.guild.interfaces;

import de.nebelniek.database.guild.model.RegionModel;
import de.nebelniek.database.interfaces.Loadable;
import de.nebelniek.database.interfaces.Saveable;

public interface IRegion extends Loadable, Saveable {


    double getCenterX();

    double getCenterY();

    double getCenterZ();

    int getRange();

    void setRange(int range);

    RegionModel getModel();

}
