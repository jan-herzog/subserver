package de.nebelniek.database.user.interfaces;

import de.nebelniek.database.guild.Guild;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.user.model.CloudUserModel;
import de.nebelniek.database.interfaces.Loadable;
import de.nebelniek.database.interfaces.Saveable;

import java.util.Date;
import java.util.UUID;

public interface ICloudUser extends Loadable, Saveable {

    UUID getUuid();

    String getLastUserName();

    long getCoins();

    Date getLastLogin();

    String getTwitchId();

    boolean isSubbed();

    IGuild getGuild();

    CloudUserModel getModel();

    void setLastUserName(String lastUserName);

    void setCoins(long coins);

    void setUuid(UUID uuid);

    void setLastLogin(Date lastLogin);

    void setTwitchId(String twitchId);

    void setSubbed(boolean subbed);

    void setGuild(IGuild guild);
}
