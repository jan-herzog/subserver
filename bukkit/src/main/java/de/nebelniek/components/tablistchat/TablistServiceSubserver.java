package de.nebelniek.components.tablistchat;

import de.nebelniek.configuration.BukkitConfiguration;
import de.nebelniek.utils.SubserverRank;
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
public class TablistServiceSubserver implements Listener {

    private final GuildManagingService guildManagingService;

    private final CloudUserManagingService cloudUserManagingService;

    private final BukkitConfiguration bukkitConfiguration;

    private Scoreboard scoreboard;

    private int i = 0;

    public void createTeams() {
        if (scoreboard.getTeam("99999Player") == null)
            scoreboard.registerNewTeam("99999Player").setPrefix(SubserverRank.DEFAULT.getPrefix() + " ");
        for (IGuild guild : guildManagingService.getGuilds()) {
            newGuild(guild);
        }
        for (Team team : scoreboard.getTeams())
            team.setColor(ChatColor.GRAY);
        update();
    }

    public void update() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(onlinePlayer.getUniqueId());
            Team team;
            if (cloudUser.getGuild() == null)
                team = scoreboard.getTeams().stream().filter(team1 -> team1.getName().contains("Player")).findAny().orElseThrow();
            else {
                team = scoreboard.getTeams().stream().filter(team1 -> team1.getName().contains(cloudUser.getGuild().getName())).findAny().orElse(null);
                if (team == null) {
                    team = scoreboard.registerNewTeam(i + cloudUser.getGuild().getName());
                    i++;
                }
                team.setPrefix(cloudUser.getGuild().getPrefix() + " ");
                team.setColor(ChatColor.GRAY);
            }
            if (!team.getPlayers().contains(onlinePlayer)) {
                if (scoreboard.getPlayerTeam(onlinePlayer) != null)
                    scoreboard.getPlayerTeam(onlinePlayer).removePlayer(onlinePlayer);
                team.addPlayer(onlinePlayer);
            }
        }
        for (Team team : scoreboard.getTeams()) {
            if (team.getName().contains("Player"))
                continue;
            if (guildManagingService.getGuildContainsName(team.getName()) == null)
                team.unregister();
        }
    }


    public void setScoreboard(Object scoreboard) {
        this.scoreboard = (Scoreboard) scoreboard;
    }

    public void newGuild(IGuild guild) {
        if (scoreboard.getTeams().stream().anyMatch(team1 -> team1.getName().contains(guild.getName()))) {
            scoreboard.getTeams().stream().filter(team1 -> team1.getName().contains(guild.getName())).findAny().get().setPrefix(guild.getPrefix() + " ");
            return;
        }
        Team team = scoreboard.registerNewTeam(i + guild.getName());
        if (guild.getPrefix() != null)
            team.setPrefix(guild.getPrefix() + " ");
        team.setColor(ChatColor.GRAY);
        update();
        i++;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(bukkitConfiguration.getPlugin(), this::update, 20L);
    }

    @Setter
    private static TablistServiceSubserver instance;

    public static TablistServiceSubserver getInstance() {
        return instance;
    }

}
