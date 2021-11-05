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
    private double centerX;

    @DatabaseField
    private double centerY;

    @DatabaseField
    private double centerZ;

    @DatabaseField
    private int range;

    public RegionModel(double centerX, double centerY, double centerZ, int range) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.range = range;
    }

}
