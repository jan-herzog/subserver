package de.nebelniek.content.tablist;

import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
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
public class TablistServiceSubserver implements Listener, TablistService {

    private final GuildManagingService guildManagingService;

    private final CloudUserManagingService cloudUserManagingService;

    @Setter
    private Scoreboard scoreboard;

    private int i = 0;

    public void createTeams() {
        for (IGuild guild : guildManagingService.getGuilds())
            newGuild(guild);
        if (scoreboard.getTeam(i + "Player") == null)
            scoreboard.registerNewTeam(i + "Player").setPrefix("§eLandstreicher §7");
        for (Team team : scoreboard.getTeams())
            team.setColor(ChatColor.GRAY);
    }

    public void update() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(onlinePlayer.getUniqueId());
            Team team;
            if (cloudUser.getGuild() == null)
                team = scoreboard.getTeams().stream().filter(team1 -> team1.getName().contains("Player")).findAny().orElseThrow();
            else
                team = scoreboard.getTeams().stream().filter(team1 -> team1.getName().contains(cloudUser.getGuild().getName())).findAny().orElseThrow();
            if (!team.getPlayers().contains(onlinePlayer)) {
                if (scoreboard.getPlayerTeam(onlinePlayer) != null)
                    scoreboard.getPlayerTeam(onlinePlayer).removePlayer(onlinePlayer);
                team.addPlayer(onlinePlayer);
            }
        }
    }

    public void newGuild(IGuild guild) {
        Team team = scoreboard.registerNewTeam(i + guild.getName());
        if (guild.getPrefix() != null)
            team.setPrefix(guild.getPrefix());
        i++;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        update();
    }

}
