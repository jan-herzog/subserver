package de.nebelniek.database.guild.interfaces;

import de.nebelniek.database.guild.GuildSettings;
import de.nebelniek.database.guild.model.GuildSettingsModel;
import de.nebelniek.database.guild.util.GuildRole;
import de.nebelniek.database.interfaces.Loadable;
import de.nebelniek.database.interfaces.Saveable;

public interface IGuildSettings extends Loadable, Saveable {

    GuildRole getManageBankAccountRole();

    GuildRole getManageRegionRole();

    GuildRole getManageMembersRole();

    void setManageBankAccountRole(GuildRole manageBankAccountRole);

    void setManageRegionRole(GuildRole manageRegionRole);

    void setManageMembersRole(GuildRole manageMembersRole);

    GuildSettingsModel getModel();

}
