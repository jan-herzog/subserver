package de.nebelniek.services.maintenance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MaintenanceKey {

    PROXY("Proxy"),
    SUBSERVER("Subserver");

    private String group;

}
