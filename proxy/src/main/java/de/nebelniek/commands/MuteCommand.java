package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import de.nebelniek.utils.Prefix;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("mute")
@CommandPermission("proxy.mute")
public class MuteCommand extends BaseCommand {


    @Default
    @CatchUnknown
    public void onDefault(ProxiedPlayer sender) {
        sender.sendMessage(Prefix.BAN + "Hilfe für §c/mute§7:");
        sender.sendMessage(Prefix.BAN + "/mute §c[User]");
        sender.sendMessage(Prefix.BAN + "/mute §c[User] [Zeit]");
        sender.sendMessage(Prefix.BAN + "/mute unmute §c[User]");
        sender.sendMessage(Prefix.BAN + "Mute/Entmute einen bestimmten §cCloudUser§7.");
    }



}
