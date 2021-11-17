package de.nebelniek.registration;

import de.nebelniek.registration.event.BukkitPluginEnableEvent;
import de.nebelniek.tablistchat.TablistServiceSubserver;
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
    public void loadOnEnable(BukkitPluginEnableEvent event) {
        tablistServiceSubserver.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        tablistServiceSubserver.createTeams();
    }

}
