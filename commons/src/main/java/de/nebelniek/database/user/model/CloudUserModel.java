package de.nebelniek.database.user.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.nebelniek.database.guild.model.GuildModel;
import de.nebelniek.database.user.ban.model.BanModel;
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
    @DatabaseField(unique = true)
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
    private String discordId;
    @DatabaseField
    private boolean subbed;
    @DatabaseField
    private String guildRole;
    @DatabaseField(columnName = "guild_id", foreign = true, foreignAutoRefresh = true)
    private GuildModel guildModel;
    @DatabaseField(columnName = "ban_id", foreign = true, foreignAutoRefresh = true)
    private BanModel ban;
    @DatabaseField(dataType = DataType.LONG_STRING)
    private String textureHash;

    public CloudUserModel(UUID uuid, String lastUserName) {
        this.uuid = uuid;
        this.lastUserName = lastUserName;
        this.lastLogin = new Date();
        this.twitchId = null;
        this.coins = 100000L;
        this.subbed = false;
        this.guildModel = null;
        this.textureHash = null;
        this.ban = null;
    }
}
