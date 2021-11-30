package de.nebelniek.database.user.ban.interfaces;

import de.nebelniek.database.interfaces.Loadable;
import de.nebelniek.database.interfaces.Saveable;
import de.nebelniek.database.user.ban.BanType;
import de.nebelniek.database.user.ban.model.BanModel;
import de.nebelniek.database.user.interfaces.ICloudUser;

import java.util.Date;

public interface IBan extends Loadable, Saveable {

    BanType getBanType();

    void setBanType(BanType banType);

    ICloudUser getCloudUser();

    Date getEndDate();

    void setEndDate(Date date);

    String getReason();

    void setReason(String reason);

    BanModel getModel();

    String toString(String banner);

}
