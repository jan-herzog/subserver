package de.nebelniek.database.user.ban.model;

import com.j256.ormlite.field.DatabaseField;
import de.nebelniek.database.user.model.CloudUserModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BanModel {

    @DatabaseField(unique = true, generatedId = true)
    private long id;

    @DatabaseField(columnName = "user_id", foreign = true, foreignAutoRefresh = true)
    private CloudUserModel cloudUserModel;

    @DatabaseField
    private String type;

    @DatabaseField
    private String reason;

    @DatabaseField
    private Date endDate;

    public BanModel(CloudUserModel cloudUserModel, String type, String reason, Date endDate) {
        this.cloudUserModel = cloudUserModel;
        this.type = type;
        this.reason = reason;
        this.endDate = endDate;
    }
}
