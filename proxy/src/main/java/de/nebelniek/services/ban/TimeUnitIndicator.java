package de.nebelniek.services.ban;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public enum TimeUnitIndicator {

    DAYS("d"),
    HOURS("h"),
    MINUTES("min");

    private String indicator;


    public static TimeUnitIndicator get(String duration) {
        if(duration == null)
            return null;
        duration = duration.replaceAll("\\d", "");
        for (TimeUnitIndicator value : values())
            if (value.getIndicator().equalsIgnoreCase(duration))
                return value;
        return null;
    }
}
