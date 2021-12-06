package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.ban.BanType;
import de.nebelniek.services.ban.BanScreen;
import de.nebelniek.services.ban.BanService;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("mute")
@CommandPermission("proxy.mute")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MuteCommand extends BaseCommand {

    private final BanService banService;

    private final CloudUserManagingService cloudUserManagingService;

    @Default
    @CatchUnknown
    public void onDefault(ProxiedPlayer sender) {
        sender.sendMessage(Prefix.BAN + "Hilfe für §c/mute§7:");
        sender.sendMessage(Prefix.BAN + "/mute §c[User]");
        sender.sendMessage(Prefix.BAN + "/mute §c[User] [Zeit]");
        sender.sendMessage(Prefix.BAN + "/mute unmute §c[User]");
        sender.sendMessage(Prefix.BAN + "Mute/Entmute einen bestimmten §cCloudUser§7.");
    }


    @Default
    @CommandCompletion("@players @nothing")
    public void onMutePerma(ProxiedPlayer sender, @Single String target, @Optional String reason) {
        cloudUserManagingService.loadUserByName(target).thenAccept(cloudUser -> {
            if (cloudUser == null) {
                sender.sendMessage(Prefix.BAN + "§cDieser CloudUser ist nicht in der Datenbank eingetragen!");
                return;
            }
            banService.createBan(cloudUser, BanType.MUTE, null, reason);
            sender.sendMessage(Prefix.BAN + "Du hast §e" + cloudUser.getLastUserName() + " §cpermanent §7gemutet.");
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(cloudUser.getUuid());
            if (player == null)
                return;
            player.sendMessage(Prefix.BAN + "§7Du wurdest §4permanent §7gemutet! " + (reason != null ? "Grund: §8" + reason : ""));
        });
    }

    @Default
    @CommandCompletion("@players @nothing")
    public void onMuteTime(ProxiedPlayer sender, @Single String target, @Single String duration, @Optional String reason) {
        cloudUserManagingService.loadUserByName(target).thenAccept(cloudUser -> {
            if (cloudUser == null) {
                sender.sendMessage(Prefix.BAN + "§cDieser CloudUser ist nicht in der Datenbank eingetragen!");
                return;
            }
            banService.createBan(cloudUser, BanType.MUTE, duration, reason).thenAccept(ban -> banService.notify(ban.toString(sender.getName())));
            sender.sendMessage(Prefix.BAN + "Du hast §e" + cloudUser.getLastUserName() + "§7 für §c" + duration + " §7gemutet.");
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(cloudUser.getUuid());
            if (player == null)
                return;
            player.sendMessage(Prefix.BAN + "Du wurdest für §e" + duration + "§c gemutet§7! " + (reason != null ? "Grund: §8" + reason : ""));
        });
    }

    @Subcommand("unmute")
    @CommandAlias("unmute")
    @CommandCompletion("@players @nothing")
    public void onUnmute(ProxiedPlayer sender, @Single String target) {
        cloudUserManagingService.loadUserByName(target).thenAccept(cloudUser -> {
            if (cloudUser == null) {
                sender.sendMessage(Prefix.BAN + "§cDieser CloudUser ist nicht in der Datenbank eingetragen!");
                return;
            }
            if (banService.isMute(cloudUser)) {
                sender.sendMessage(Prefix.BAN + "§cDieser CloudUser ist nicht gemutet!");
                return;
            }
            if (banService.unban(cloudUser)) {
                sender.sendMessage(Prefix.BAN + "Du hast §e" + cloudUser.getLastUserName() + "§a entmutet§7.");
                return;
            }
        });
    }

}
