package de.nebelniek.database.guild.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.nebelniek.database.guild.util.GuildRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@DatabaseTable(tableName = "guildsettings_data")
public class GuildSettingsModel {

    @DatabaseField(unique = true, generatedId = true)
    private long id;

    @DatabaseField
    private String manageBankAccountRole;

    @DatabaseField
    private String manageRegionRole;

    @DatabaseField
    private String manageMembersRole;

    public GuildSettingsModel() {
        this.manageBankAccountRole = GuildRole.ADMIN.name();
        this.manageRegionRole = GuildRole.ADMIN.name();
        this.manageMembersRole = GuildRole.ADMIN.name();
    }


}
