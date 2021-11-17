package de.nebelniek.database.guild.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import de.nebelniek.database.user.model.CloudUserModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "guild_data")
public class GuildModel {

    @DatabaseField(unique = true, generatedId = true)
    private long id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String color;

    @DatabaseField
    private String prefix;

    @DatabaseField
    private String home;

    @DatabaseField
    private long balance;

    @DatabaseField(columnName = "region_id", foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private RegionModel regionModel;

    @DatabaseField(columnName = "settings_id", foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private GuildSettingsModel settingsModel;

    @DatabaseField(columnName = "member")
    private String member;

    @DatabaseField(columnName = "allies")
    private String allies;

    public GuildModel(String name) {
        this.name = name;
        this.color = "§a";
        this.prefix = "&a" + name;
        this.balance = 0L;
        this.regionModel = null;
        this.settingsModel = new GuildSettingsModel();
        this.member = null;
        this.allies = null;
    }
}
