package de.nebelniek.content.guild.invite.ally;

import de.nebelniek.content.guild.GuildContentService;
import de.nebelniek.content.guild.chat.GuildChatService;
import de.nebelniek.content.guild.invite.InviteEntry;
import de.nebelniek.content.guild.response.GuildContentResponse;
import de.nebelniek.content.guild.response.GuildResponseState;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.util.GuildRole;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
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
public class GuildInviteAllyService implements Listener {

    private final CloudUserManagingService cloudUserManagingService;
    private final GuildManagingService guildManagingService;
    private final GuildChatService guildChatService;

    private final GuildContentService guildContentService;

    private final Map<IGuild, List<InviteEntry>> pendingInvites = new HashMap<>();

    public GuildContentResponse invite(IGuild guild, IGuild other, ICloudUser inviter) {
        if (!pendingInvites.containsKey(other))
            pendingInvites.put(other, new ArrayList<>());
        if (pendingInvites.get(other).stream().anyMatch(inviteEntry -> inviteEntry.guild().equals(guild)))
            return new GuildContentResponse(GuildResponseState.ERROR, "Zu diesem Spieler steht bereits eine Einladung offen!");
        pendingInvites.get(other).add(new InviteEntry(guild, inviter));
        if (guildChatService.someoneOnline(other)) {
            guildChatService.sendAnnouncementToRole(guild, GuildRole.ADMIN, Prefix.GUILD + "Deiner Gilde von §e" + inviter.getLastUserName() + "§7 ein Verbündungsantrag mit der Gilde " + guild.getColor() + guild.getName() + " §7gestellt!");
            guildChatService.sendAnnouncementToRole(other, GuildRole.ADMIN,
                    Component.text(Prefix.GUILD.toString())
                            .append(Component.text("§7[§a§lAnnehmen§7]").clickEvent(ClickEvent.runCommand("guild ally accept " + guild.getName())))
                            .append(Component.text(" §8oder "))
                            .append(Component.text("§7[§a§lAblehnen§7]").clickEvent(ClickEvent.runCommand("guild ally deny " + guild.getName())))
            );
            return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast " + other.getColor() + other.getName() + "§7 ein §aVerbündungsantrag§7 geschickt!");
        }
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast " + other.getColor() + other.getName() + "§7 ein §aVerbündungsantrag§7 geschickt! (Keiner der Mitglieder ist gerade online, sie werden benachrichtigt, falls sie beitreten)");
    }

    public GuildContentResponse accept(IGuild other, IGuild guild) {
        InviteEntry inviteEntry = pendingInvites.get(guild).stream().filter(entry -> entry.guild().equals(guild)).findFirst().orElse(null);
        if (inviteEntry == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Invite existiert nicht!");
        pendingInvites.remove(guild);
        guild.getAllies().add(other);
        other.getAllies().add(guild);
        guildChatService.sendAnnouncement(guild, "Deine Gilde ist nun mit " + other.getColor() + other.getName() + " §averbündet§7!");
        guildChatService.sendAnnouncement(other, "Deine Gilde ist nun mit " + guild.getColor() + guild.getName() + " §averbündet§7!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast den §2Verbündungsantrag§7 von " + guild.getColor() + guild.getName() + " §aangenommen§7!");
    }

    public GuildContentResponse deny(IGuild other, IGuild guild) {
        InviteEntry inviteEntry = pendingInvites.get(guild).stream().filter(entry -> entry.guild().equals(guild)).findFirst().orElse(null);
        if (inviteEntry == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Invite existiert nicht!");
        pendingInvites.get(guild).remove(inviteEntry);
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast den §2Verbündungsantrag§7 von " + guild.getColor() + guild.getName() + " §cabgelehnt§7!");
    }

    public GuildContentResponse openInvites(IGuild other, IGuild guild) {
        if (pendingInvites.get(guild).size() == 0)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du hast keine offenen Verbündungsanträge!");
        StringBuilder stringBuilder = new StringBuilder();
        for (InviteEntry inviteEntry : pendingInvites.get(guild)) {
            if (!stringBuilder.toString().equalsIgnoreCase(""))
                stringBuilder.append("§7, ");
            stringBuilder.append(inviteEntry.guild().getColor()).append(inviteEntry.guild().getName()).append("§7(Von §e").append(inviteEntry.inviter().getLastUserName()).append("§7)");
        }
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Offene Verbündungsanträge: " + stringBuilder);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(event.getPlayer().getUniqueId());
        IGuild guild = cloudUser.getGuild();
        if (!pendingInvites.containsKey(guild))
            return;
        if (pendingInvites.get(guild).size() == 0)
            return;
        if (cloudUser.getGuildRole().isHigherOrEquals(GuildRole.ADMIN))
            event.getPlayer().sendMessage(Prefix.GUILD + "Deine Gilde hat §e" + pendingInvites.get(guild).size() + " §7offene §eVerbündungsanträge§7. Schaue sie dir mit §2/guild §aally invites §7an.");
    }


}
