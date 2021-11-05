package de.nebelniek.database.user.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.nebelniek.database.guild.model.GuildModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "clouduser_data")
public class CloudUserModel {

    @DatabaseField(unique = true, generatedId = true)
    private long id;
    @DatabaseField
    private UUID uuid;
    @DatabaseField
    private String lastUserName;
    @DatabaseField
    private long coins;
    @DatabaseField
    private Date lastLogin;
    @DatabaseField
    private String twitchId;
    @DatabaseField
    private boolean subbed;
    @DatabaseField
    private String guildRole;
    @DatabaseField(columnName = "guild_id", foreign = true, foreignAutoRefresh = true)
    private GuildModel guildModel;

    public CloudUserModel(UUID uuid, String lastUserName) {
        this.uuid = uuid;
        this.lastUserName = lastUserName;
        this.lastLogin = new Date();
        this.twitchId = null;
        this.coins = 0;
        this.subbed = false;
        this.guildModel = null;
    }
}
