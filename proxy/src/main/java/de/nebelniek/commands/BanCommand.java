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
@CommandAlias("ban")
@CommandPermission("proxy.ban")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BanCommand extends BaseCommand {

    private final BanService banService;

    private final CloudUserManagingService cloudUserManagingService;

    @Default
    @CatchUnknown
    public void onDefault(ProxiedPlayer sender) {
        sender.sendMessage(Prefix.BAN + "Hilfe für §c/ban§7:");
        sender.sendMessage(Prefix.BAN + "/ban §c[User] (Grund)");
        sender.sendMessage(Prefix.BAN + "/ban §c[User] [Zeit] (Grund)");
        sender.sendMessage(Prefix.BAN + "/ban unban §c[User]");
        sender.sendMessage(Prefix.BAN + "Banne/Entbanne einen bestimmten §cCloudUser§7.");
    }

    @Default
    @CommandCompletion("@players @nothing")
    public void onBanPerma(ProxiedPlayer sender, @Single String target, @Optional String reason) {
        cloudUserManagingService.loadUserByName(target).thenAccept(cloudUser -> {
            if (cloudUser == null) {
                sender.sendMessage(Prefix.BAN + "§cDieser CloudUser ist nicht in der Datenbank eingetragen!");
                return;
            }
            if (cloudUser.getBan() != null)
                sender.sendMessage(Prefix.BAN + "§aBan für §e" + cloudUser.getLastUserName() + " §awird geupdated!");
            banService.createBan(cloudUser, BanType.PROXY_BAN, null, reason);
            sender.sendMessage(Prefix.BAN + "Du hast §e" + cloudUser.getLastUserName() + " §cpermanent §7gebannt.");
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(cloudUser.getUuid());
            if (player == null)
                return;
            player.disconnect(new TextComponent(BanScreen.perma(reason)));
        });
    }

    @Default
    @CommandCompletion("@players @nothing")
    public void onBanTime(ProxiedPlayer sender, @Single String target, @Single String duration, @Optional String reason) {
        cloudUserManagingService.loadUserByName(target).thenAccept(cloudUser -> {
            if (cloudUser == null) {
                sender.sendMessage(Prefix.BAN + "§cDieser CloudUser ist nicht in der Datenbank eingetragen!");
                return;
            }
            banService.createBan(cloudUser, BanType.PROXY_BAN, duration, reason).thenAccept(ban -> banService.notify(ban.toString(sender.getName())));
            sender.sendMessage(Prefix.BAN + "Du hast §e" + cloudUser.getLastUserName() + "§7 für §c" + duration + " §7gebannt.");
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(cloudUser.getUuid());
            if (player == null)
                return;
            player.disconnect(new TextComponent(BanScreen.time(duration, reason)));
        });
    }

    @Subcommand("unban")
    @CommandAlias("unban")
    @CommandCompletion("@players @nothing")
    public void onUnban(ProxiedPlayer sender, @Single String target) {
        cloudUserManagingService.loadUserByName(target).thenAccept(cloudUser -> {
            if (cloudUser == null) {
                sender.sendMessage(Prefix.BAN + "§cDieser CloudUser ist nicht in der Datenbank eingetragen!");
                return;
            }
            if (banService.unban(cloudUser)) {
                sender.sendMessage(Prefix.BAN + "Du hast §e" + cloudUser.getLastUserName() + "§a entbannt§7.");
                return;
            }
            sender.sendMessage(Prefix.BAN + "§cDieser CloudUser ist nicht gebannt!");
        });
    }

}
