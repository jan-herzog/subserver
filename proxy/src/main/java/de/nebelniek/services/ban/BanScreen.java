package de.nebelniek.services.ban;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BanScreen {

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd 'um' HH:mm");

    public static String perma(String reason) {
        return time(null, reason);
    }

    public static String time(String duration, String reason) {
        if (duration == null)
            return """
                    §7-------------------------
                                                
                    §e§lNebelniek Subserver
                                                
                    Du wurdest §cPermanent §7vom Server §cgesperrt§7!
                                                
                    §7Grund: §8%s
                                                
                    §7-------------------------
                    """.formatted(reason == null ? "Nicht angegeben!" : reason);
        return """
                §7-------------------------
                                            
                §e§lNebelniek Subserver
                                            
                Du wurdest für §c%s §7vom Server §cgesperrt§7!
                                            
                §7Grund: §8%s
                                            
                §7-------------------------
                """.formatted(duration, reason == null ? "Nicht angegeben!" : reason);
    }

    public static String timeLeft(Date date, String reason) {
        return """
                §7-------------------------
                                            
                §e§lNebelniek Subserver
                                            
                Du bist vom Server §cgesperrt§7!
                §7Entbannungsdatum: §8%s
                                            
                §7Grund: §8%s
                                            
                §7-------------------------
                """.formatted(format.format(date), reason == null ? "Nicht angegeben!" : reason);
    }

    public static String ingameKick(String duration, String reason) {
        return "§7Du wurdest für §c" + duration + " §7vom Server §cgesperrt§7! 7Grund: §8" + reason;
    }


}
