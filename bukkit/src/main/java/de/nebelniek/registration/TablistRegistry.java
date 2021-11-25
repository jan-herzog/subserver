package de.nebelniek.registration;

import de.nebelniek.registration.event.GuildsLoadedEvent;
import de.nebelniek.components.tablistchat.TablistServiceSubserver;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TablistRegistry {

    private final TablistServiceSubserver tablistServiceSubserver;

    @EventListener
    public void loadOnEnable(GuildsLoadedEvent event) {
        Bukkit.getPluginManager().registerEvents(tablistServiceSubserver, event.getPlugin());
        tablistServiceSubserver.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        tablistServiceSubserver.createTeams();
    }

}
