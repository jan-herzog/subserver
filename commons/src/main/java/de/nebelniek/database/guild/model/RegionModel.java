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
    private double aX;

    @DatabaseField
    private double aZ;

    @DatabaseField
    private double bX;

    @DatabaseField
    private double bZ;

    public RegionModel(double aX, double aZ, double bX, double bZ) {
        this.aX = aX;
        this.aZ = aZ;
        this.bX = bX;
        this.bZ = bZ;
    }
}
