package de.nebelniek.components.scoreboard;


import de.nebelniek.configuration.LobbyConfiguration;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import fr.mrmicky.fastboard.FastBoard;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScoreboardManagementService implements Listener {

    private final CloudUserManagingService cloudUserManagingService;
    private final LobbyConfiguration configuration;

    private final Map<ICloudUser, FastBoard> fastBoards = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        cloudUserManagingService.loadUser(player.getUniqueId()).thenAccept(cloudUser -> fastBoards.put(cloudUser, createBoard(cloudUser, player)));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ICloudUser cloudUser = fastBoards.entrySet().stream().filter(entry -> entry.getKey().getUuid().equals(player.getUniqueId())).findFirst().get().getKey();
        FastBoard board = fastBoards.get(cloudUser);
        board.delete();
        fastBoards.remove(cloudUser);
    }

    private FastBoard createBoard(ICloudUser cloudUser, Player player) {
        FastBoard board = new FastBoard(player);
        board.updateTitle("§5§lSubserver");
        board.updateLines(
                "",
                "§8● §dProfil",
                " §7➥ " + (cloudUser.getGuildRole() != null ? cloudUser.getGuildRole().getColor() : "§7") + player.getName(),
                "",
                "§8● §dCoins",
                " §7➥ " + cloudUser.getCoins() + "$",
                "",
                "§8● §dGilde",
                " §7➥ " + (cloudUser.getGuild() != null ? cloudUser.getGuild().getColor() + cloudUser.getGuild().getName() : "§7Keine Gilde")
        );
        return board;
    }

    public void updateCoins(ICloudUser cloudUser) {
        System.out.println(fastBoards);
        System.out.println(cloudUser);
        fastBoards.get(cloudUser).updateLine(5, " §7➥ " + cloudUser.getCoins() + "$");
    }

    public void updateProfile(ICloudUser cloudUser) {
        fastBoards.get(cloudUser).updateLine(2, " §7➥ " + (cloudUser.getGuildRole() != null ? cloudUser.getGuildRole().getColor() : "§7") + cloudUser.getLastUserName());
    }

}
