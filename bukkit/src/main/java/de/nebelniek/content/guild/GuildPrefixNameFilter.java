package de.nebelniek.content.guild;

import org.springframework.stereotype.Component;

@Component
public class GuildPrefixNameFilter {

    private String[] array = {
            "nigger",
            "admin",
            "administrator",
            "nega",
            "neger",
            "nibba",
            "hurensohn"
    };

    public boolean contains(String toTest) {
        for (String s : array) {
            if (toTest.toUpperCase().contains(s.toUpperCase()))
                return true;
        }
        return false;
    }

}
