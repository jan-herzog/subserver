package de.nebelniek.database.guild.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "region_data")
public class RegionModel {

    @DatabaseField(unique = true, generatedId = true)
    private long id;

    @DatabaseField
    private String world;

    @DatabaseField
    private double aX;

    @DatabaseField
    private double aZ;

    @DatabaseField
    private double bX;

    @DatabaseField
    private double bZ;

    public RegionModel(String world, double aX, double aZ, double bX, double bZ) {
        this.world = world;
        this.aX = aX;
        this.aZ = aZ;
        this.bX = bX;
        this.bZ = bZ;
    }
}
