package de.nebelniek.database.user;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CloudUser {

    @Id
    @GeneratedValue/*generator = "clouduser_id_gen"*/
    //@SequenceGenerator(name = "clouduser_id_gen", initialValue = 1500, allocationSize = 1)
    private long id;
    private UUID uuid;
    private String lastUserName;
    private Date lastLogin;
    private String twitchId;
    private boolean subbed;

}
