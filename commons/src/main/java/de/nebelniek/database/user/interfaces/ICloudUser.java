package de.nebelniek.database.user.interfaces;

import de.nebelniek.database.guild.Guild;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.util.GuildRole;
import de.nebelniek.database.user.ban.Ban;
import de.nebelniek.database.user.ban.interfaces.IBan;
import de.nebelniek.database.user.model.CloudUserModel;
import de.nebelniek.database.interfaces.Loadable;
import de.nebelniek.database.interfaces.Saveable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ICloudUser extends Loadable, Saveable {

    UUID getUuid();

    String getLastUserName();

    long getCoins();

    Date getLastLogin();

    String getTwitchId();

    String getDiscordId();

    boolean isSubbed();

    IGuild getGuild();

    GuildRole getGuildRole();

    String getTextureHash();

    IBan getBan();

    CloudUserModel getModel();

    void setLastUserName(String lastUserName);

    void setCoins(long coins);

    void setUuid(UUID uuid);

    void setLastLogin(Date lastLogin);

    void setTwitchId(String twitchId);

    void setDiscordId(String discordId);

    void setSubbed(boolean subbed);

    void setGuild(IGuild guild);

    void setGuildRole(GuildRole guildRole);

    void setTextureHash(String textureHash);

    void setBan(IBan ban);
}
