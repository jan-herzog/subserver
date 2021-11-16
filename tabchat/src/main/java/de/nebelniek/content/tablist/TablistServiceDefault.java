package de.nebelniek.content.tablist;

import de.nebelniek.utils.NameUtils;
import de.nebelniek.utils.TablistService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TablistServiceDefault implements Listener, TablistService {

    private Scoreboard scoreboard;

    public void createTeams() {
        if (scoreboard.getTeam("001Admin") == null)
            scoreboard.registerNewTeam("001Admin").setPrefix(NameUtils.getPrefix("administrator") + " §7");
        if (scoreboard.getTeam("002Team") == null)
            scoreboard.registerNewTeam("002Team").setPrefix(NameUtils.getPrefix("team") + " §7");
        if (scoreboard.getTeam("003Mod") == null)
            scoreboard.registerNewTeam("003Mod").setPrefix(NameUtils.getPrefix("mod") + " §7");
        if (scoreboard.getTeam("004Sub") == null)
            scoreboard.registerNewTeam("004Sub").setPrefix(NameUtils.getPrefix("sub") + " §7");
        if (scoreboard.getTeam("005Player") == null)
            scoreboard.registerNewTeam("005Player").setPrefix(NameUtils.getPrefix("default") + " §7");
        for (Team team : scoreboard.getTeams())
            team.setColor(ChatColor.GRAY);
    }

    public void update() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Team team = scoreboard.getTeam(scoreboardTeamName(NameUtils.getPrefix(onlinePlayer.getUniqueId())));
            if (!team.getPlayers().contains(onlinePlayer)) {
                if (scoreboard.getPlayerTeam(onlinePlayer) != null)
                    scoreboard.getPlayerTeam(onlinePlayer).removePlayer(onlinePlayer);
                team.addPlayer(onlinePlayer);
            }
        }
    }

    @Override
    public void setScoreboard(Object scoreboard) {
        this.scoreboard = (Scoreboard) scoreboard;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        update();
    }

    private String scoreboardTeamName(String prefix) {
        if (prefix.equals(NameUtils.getPrefix("administrator")))
            return "001Admin";
        if (prefix.equals(NameUtils.getPrefix("team")))
            return "002Team";
        if (prefix.equals(NameUtils.getPrefix("mod")))
            return "003Mod";
        if (prefix.equals(NameUtils.getPrefix("sub")))
            return "004Sub";
        if (prefix.equals(NameUtils.getPrefix("default")))
            return "005Player";
        return "-";
    }

}
