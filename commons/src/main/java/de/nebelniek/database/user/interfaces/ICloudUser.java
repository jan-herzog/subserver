package de.nebelniek.database.user.interfaces;

import de.nebelniek.database.user.model.CloudUserModel;

import java.util.Date;
import java.util.UUID;

public interface ICloudUser extends Loadable, Saveable {

    UUID getUuid();

    String getLastUserName();

    long getCoins();

    Date getLastLogin();

    String getTwitchId();

    boolean isSubbed();

    CloudUserModel getModel();

    void setLastUserName(String lastUserName);

    void setCoins(long coins);

    void setUuid(UUID uuid);

    void setLastLogin(Date lastLogin);

    void setTwitchId(String twitchId);

    void setSubbed(boolean subbed);

}
