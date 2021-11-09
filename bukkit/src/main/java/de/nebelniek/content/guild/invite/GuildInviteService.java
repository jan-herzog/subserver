package de.nebelniek.content.guild.invite;

import de.nebelniek.content.guild.GuildContentService;
import de.nebelniek.content.guild.response.GuildContentResponse;
import de.nebelniek.content.guild.response.GuildResponseState;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuildInviteService implements Listener {

    private final CloudUserManagingService cloudUserManagingService;

    private final GuildContentService guildContentService;

    private final Map<ICloudUser, List<InviteEntry>> pendingInvites = new HashMap<>();

    public GuildContentResponse invite(IGuild guild, ICloudUser cloudUser, ICloudUser inviter) {
        if (cloudUser.getGuild() != null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Spieler ist bereits in einer Gilde!");
        if (!pendingInvites.containsKey(cloudUser))
            pendingInvites.put(cloudUser, new ArrayList<>());
        if (pendingInvites.get(cloudUser).stream().anyMatch(inviteEntry -> inviteEntry.guild().equals(guild)))
            return new GuildContentResponse(GuildResponseState.ERROR, "Zu diesem Spieler steht bereits eine Einladung offen!");
        Player player = Bukkit.getPlayer(cloudUser.getUuid());
        pendingInvites.get(cloudUser).add(new InviteEntry(guild, inviter));
        if (player != null) {
            player.sendMessage(Prefix.GUILD + "Du wurdest von §e" + inviter.getLastUserName() + "§7 in die Gilde " + guild.getColor() + guild.getName() + " §7eingeladen!");
            player.sendMessage(
                    Component.text(Prefix.GUILD.toString())
                            .append(Component.text("§7[§a§lAnnehmen§7]").clickEvent(ClickEvent.runCommand("guild accept " + guild.getName())))
                            .append(Component.text(" §8oder "))
                            .append(Component.text("§7[§a§lAblehnen§7]").clickEvent(ClickEvent.runCommand("guild deny " + guild.getName())))
            );
            return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast den Spieler §e" + cloudUser.getLastUserName() + " eingeladen!");
        }
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast dem Offline-Spieler §e" + cloudUser.getLastUserName() + " eingeladen! Er wird benachrichtigt, sobald er online ist!");
    }

    public GuildContentResponse accept(IGuild guild, ICloudUser cloudUser) {
        InviteEntry inviteEntry = pendingInvites.get(cloudUser).stream().filter(entry -> entry.guild().equals(guild)).findFirst().orElse(null);
        if (inviteEntry == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Invite existiert nicht!");
        pendingInvites.remove(cloudUser);
        guildContentService.joinGuild(cloudUser, guild, inviteEntry.inviter());
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast die Einladung von " + guild.getColor() + guild.getName() + " §aangenommen§7!");
    }

    public GuildContentResponse deny(IGuild guild, ICloudUser cloudUser) {
        InviteEntry inviteEntry = pendingInvites.get(cloudUser).stream().filter(entry -> entry.guild().equals(guild)).findFirst().orElse(null);
        if (inviteEntry == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Invite existiert nicht!");
        pendingInvites.get(cloudUser).remove(inviteEntry);
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast die Einladung von " + guild.getColor() + guild.getName() + " §cabgelehnt§7!");
    }

    public GuildContentResponse openInvites(ICloudUser cloudUser) {
        if (pendingInvites.get(cloudUser).size() == 0)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du hast keine offenen Einladungen!");
        StringBuilder stringBuilder = new StringBuilder();
        for (InviteEntry inviteEntry : pendingInvites.get(cloudUser)) {
            if (!stringBuilder.toString().equalsIgnoreCase(""))
                stringBuilder.append("§7, ");
            stringBuilder.append(inviteEntry.guild().getColor()).append(inviteEntry.guild().getName()).append("§7(Von §e").append(inviteEntry.inviter().getLastUserName()).append("§7)");
        }
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Offene Einladungen: " + stringBuilder);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(event.getPlayer().getUniqueId());
        if (!pendingInvites.containsKey(cloudUser))
            return;
        if (pendingInvites.get(cloudUser).size() == 0)
            return;
        event.getPlayer().sendMessage(Prefix.GUILD + "Du hast §e" + pendingInvites.get(cloudUser).size() + " §7offene §eEinladungen§7. Schaue sie dir mit §2/guild §ainvites §7an.");
    }


}
