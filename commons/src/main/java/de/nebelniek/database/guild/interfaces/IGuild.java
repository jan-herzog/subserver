package de.nebelniek.database.guild.interfaces;

import com.j256.ormlite.dao.ForeignCollection;
import de.nebelniek.database.guild.model.GuildModel;
import de.nebelniek.database.guild.util.HomePoint;
import de.nebelniek.database.interfaces.Loadable;
import de.nebelniek.database.interfaces.Saveable;
import de.nebelniek.database.user.interfaces.ICloudUser;

import java.util.List;

public interface IGuild extends Loadable, Saveable {

    String getName();

    void setName(String name);

    String getColor();

    void setColor(String color);

    String getPrefix();

    void setPrefix(String prefix);

    long getBalance();

    String getDiscordRole();

    void setDiscordRole(String discordRole);

    void setBalance(long balance);

    void setHome(HomePoint home);

    IRegion getRegion();

    ICloudUser getOwner();

    void setRegion(IRegion region);

    IGuildSettings getSettings();

    List<ICloudUser> getMember();

    List<IGuild> getAllies();

    HomePoint getHome();

    GuildModel getModel();

}
