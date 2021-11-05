package de.nebelniek.database.user.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "guild_data")
public class GuildModel {

    @DatabaseField
    private String name;

    @DatabaseField
    private String prefix;

    @DatabaseField
    private List<CloudUserModel> member;



}
