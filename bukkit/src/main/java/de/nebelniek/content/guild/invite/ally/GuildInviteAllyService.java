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

    private final GuildChatService guildChatService;

    private final GuildContentService guildContentService;

    private final Map<IGuild, List<InviteEntry>> pendingInvites = new HashMap<>();

    public GuildContentResponse invite(IGuild guild, IGuild other, ICloudUser inviter) {
        if (other == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Diese Gilde existiert nicht!");
        if (guild.equals(other))
            return new GuildContentResponse(GuildResponseState.ERROR, "Du kannst deiner eigenen Gilde keinen Verbündungsantrag schicken!");
        if (!inviter.getGuildRole().isHigherOrEquals(GuildRole.ADMIN))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (!pendingInvites.containsKey(other))
            pendingInvites.put(other, new ArrayList<>());
        if (pendingInvites.get(other).stream().anyMatch(inviteEntry -> inviteEntry.guild().equals(guild)))
            return new GuildContentResponse(GuildResponseState.ERROR, "Zu diesem Spieler steht bereits eine Einladung offen!");
        pendingInvites.get(other).add(new InviteEntry(guild, inviter));
        if (guildChatService.someoneOnline(other)) {
            guildChatService.sendAnnouncementToRole(other, GuildRole.ADMIN, "Deiner Gilde wurde von §e" + inviter.getLastUserName() + "§7 ein Verbündungsantrag mit der Gilde " + guild.getColor() + guild.getName() + " §7gestellt!");
            guildChatService.sendAnnouncementToRole(other, GuildRole.ADMIN,
                    Component.text("§7[§a§lAnnehmen§7]").clickEvent(ClickEvent.runCommand("/guild ally accept " + guild.getName()))
                            .append(Component.text(" §7oder "))
                            .append(Component.text("§7[§a§lAblehnen§7]").clickEvent(ClickEvent.runCommand("/guild ally deny " + guild.getName())))
            );
            return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast " + other.getColor() + other.getName() + "§7 ein §aVerbündungsantrag§7 geschickt!");
        }
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast " + other.getColor() + other.getName() + "§7 ein §aVerbündungsantrag§7 geschickt! (Keiner der Mitglieder ist gerade online, sie werden benachrichtigt, falls sie beitreten)");
    }

    public GuildContentResponse accept(IGuild other, IGuild guild) {
        if (!pendingInvites.containsKey(guild) || pendingInvites.get(guild).size() == 0)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du hast keine offenen Verbündungsanträge!");
        InviteEntry inviteEntry = pendingInvites.get(guild).stream().filter(entry -> entry.guild().equals(other)).findFirst().orElse(null);
        if (inviteEntry == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Invite existiert nicht!");
        pendingInvites.remove(guild);
        guild.getAllies().add(other);
        other.getAllies().add(guild);
        guild.saveAsync();
        other.saveAsync();
        guildChatService.sendAnnouncement(guild, "Deine Gilde ist nun mit " + other.getColor() + other.getName() + " §averbündet§7!");
        guildChatService.sendAnnouncement(other, "Deine Gilde ist nun mit " + guild.getColor() + guild.getName() + " §averbündet§7!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast den §2Verbündungsantrag§7 von " + other.getColor() + other.getName() + " §aangenommen§7!");
    }

    public GuildContentResponse deny(IGuild other, IGuild guild) {
        if (!pendingInvites.containsKey(guild) || pendingInvites.get(guild).size() == 0)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du hast keine offenen Verbündungsanträge!");
        InviteEntry inviteEntry = pendingInvites.get(guild).stream().filter(entry -> entry.guild().equals(other)).findFirst().orElse(null);
        if (inviteEntry == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Invite existiert nicht!");
        pendingInvites.get(guild).remove(inviteEntry);
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast den §2Verbündungsantrag§7 von " + guild.getColor() + guild.getName() + " §cabgelehnt§7!");
    }

    public GuildContentResponse openInvites(IGuild guild) {
        if (guild == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!pendingInvites.containsKey(guild) || pendingInvites.get(guild).size() == 0)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du hast keine offenen Verbündungsanträge!");
        StringBuilder stringBuilder = new StringBuilder();
        for (InviteEntry inviteEntry : pendingInvites.get(guild)) {
            if (!stringBuilder.toString().equalsIgnoreCase(""))
                stringBuilder.append("§7, ");
            stringBuilder.append(inviteEntry.guild().getColor()).append(inviteEntry.guild().getName()).append("§7(Von §e").append(inviteEntry.inviter().getLastUserName()).append("§7)");
        }
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Offene Verbündungsanträge: " + stringBuilder);
    }

    public GuildContentResponse disconnectAlly(ICloudUser cloudUser, IGuild other) {
        IGuild guild = cloudUser.getGuild();
        if (other == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Diese Gilde existiert nicht!");
        if (guild == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!guild.getAllies().contains(other))
            return new GuildContentResponse(GuildResponseState.ERROR, "Deine Gilde ist nicht mit dieser Gilde verbündet!");
        if (!cloudUser.getGuildRole().isHigherOrEquals(GuildRole.ADMIN))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        guild.getAllies().remove(other);
        other.getAllies().remove(guild);
        guild.saveAsync();
        other.saveAsync();
        guildChatService.sendAnnouncement(other, guild.getColor() + guild.getName() + " §chat die §aVerbündung§c mit dieser Gilde aufgehoben!");
        guildChatService.sendAnnouncement(guild, cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + " §chat die §aVerbündung§c mit " + other.getColor() + other.getName() + " §caufgehoben!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast deine Gilde von " + other.getColor() + other.getName() + "§c getrennt§7!");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> {
            IGuild guild = cloudUser.getGuild();
            if (!pendingInvites.containsKey(guild))
                return;
            if (pendingInvites.get(guild).size() == 0)
                return;
            if (cloudUser.getGuildRole().isHigherOrEquals(GuildRole.ADMIN))
                event.getPlayer().sendMessage(Prefix.GUILD + "Deine Gilde hat §e" + pendingInvites.get(guild).size() + " §7offene §eVerbündungsanträge§7. Schaue sie dir mit §2/guild §aally invites §7an.");
        });
    }


}
