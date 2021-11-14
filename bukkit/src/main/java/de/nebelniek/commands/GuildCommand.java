package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.nebelniek.configuration.BukkitConfiguration;
import de.nebelniek.content.guild.BalanceAction;
import de.nebelniek.content.guild.GuildContentService;
import de.nebelniek.content.guild.chat.GuildChatService;
import de.nebelniek.content.guild.invite.GuildInviteService;
import de.nebelniek.content.guild.invite.ally.GuildInviteAllyService;
import de.nebelniek.content.guild.response.GuildContentResponse;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.inventory.guild.GuildMainMenu;
import de.nebelniek.inventory.guild.NoGuildMenu;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("guild")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuildCommand extends BaseCommand {

    private final CloudUserManagingService cloudUserManagingService;

    private final GuildManagingService guildManagingService;

    private final GuildContentService service;

    private final GuildChatService guildChatService;

    private final GuildInviteService guildInviteService;

    private final GuildInviteAllyService guildInviteAllyService;

    private final BukkitConfiguration configuration;

    @Default
    public void onDefault(Player sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> {
            if (cloudUser.getGuild() != null) {
                Bukkit.getScheduler().runTask(configuration.getPlugin(), () -> new GuildMainMenu(cloudUser.getGuild()).open(sender));
                return;
            }
            Bukkit.getScheduler().runTask(configuration.getPlugin(), () -> new NoGuildMenu().open(sender));
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("help")
    @CatchUnknown
    public void help(Player sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> {
            sender.sendMessage(Prefix.GUILD + "Hilfe für §a/guild§7:");
            sender.sendMessage(Prefix.GUILD + "/guild");
            sender.sendMessage(Prefix.GUILD + "Öffnet das Guild-Menu");
            sender.sendMessage(Prefix.GUILD + "/guild §acreate§2 [name]");
            sender.sendMessage(Prefix.GUILD + "Erstellt eine Gilde | Kosten: 10k");
            sender.sendMessage(Prefix.GUILD + "/guild §aleave§2 [name]");
            sender.sendMessage(Prefix.GUILD + "Verlasse deine Gilde");
            sender.sendMessage(Prefix.GUILD + "/guild §ainvite§2 [Spieler]");
            sender.sendMessage(Prefix.GUILD + "Lade einen Spieler in deine Gilde ein");
            sender.sendMessage(Prefix.GUILD + "/guild §aaccept§2 [Gilde]");
            sender.sendMessage(Prefix.GUILD + "Nehme eine Einladung an");
            sender.sendMessage(Prefix.GUILD + "/guild §adeny§2 [Gilde]");
            sender.sendMessage(Prefix.GUILD + "Lehne eine Einladung ab");
            if (cloudUser.getGuild() != null) {
                sender.sendMessage(Prefix.GUILD + "/guild §arename§2 [name]");
                sender.sendMessage(Prefix.GUILD + "Nenne deine Gilde um | Kosten: 5k");
                sender.sendMessage(Prefix.GUILD + "/guild §achangeprefix§2 [prefix]");
                sender.sendMessage(Prefix.GUILD + "Setzt den Prefix | Kosten: 15k");
                sender.sendMessage(Prefix.GUILD + "/guild §achat§2 [Nachricht]");
                sender.sendMessage(Prefix.GUILD + "Alias: §a/gc");
                sender.sendMessage(Prefix.GUILD + "Sende deinen Kollegen eine Nachricht");
                sender.sendMessage(Prefix.GUILD + "/guild §abank");
                sender.sendMessage(Prefix.GUILD + "/guild §abank deposit§2 [Wert]");
                sender.sendMessage(Prefix.GUILD + "/guild §abank withdraw§2 [Wert]");
                sender.sendMessage(Prefix.GUILD + "Benutze das Gilden-Konto");
                sender.sendMessage(Prefix.GUILD + "/guild §asethome");
                sender.sendMessage(Prefix.GUILD + "Setzte deiner Gilde einen Homepunkt | Kosten: 5k");
                sender.sendMessage(Prefix.GUILD + "/guild §ahome");
                sender.sendMessage(Prefix.GUILD + "Teleportiert dich nach Hause | Kosten: 500");
                sender.sendMessage(Prefix.GUILD + "/guild §aclaim");
                sender.sendMessage(Prefix.GUILD + "Beanspruche eine Region für deine Gilde | Kosten: 5k");
                sender.sendMessage(Prefix.GUILD + "/guild §akick§2 [Spieler]");
                sender.sendMessage(Prefix.GUILD + "Kicke ein Mitglied");
                sender.sendMessage(Prefix.GUILD + "/guild §aally invite§2 [Gilde]");
                sender.sendMessage(Prefix.GUILD + "Stelle einen Verbündungsantrag");
                sender.sendMessage(Prefix.GUILD + "/guild §aally accept§2 [Gilde]");
                sender.sendMessage(Prefix.GUILD + "Nehme einen Verbündungsantrag an");
                sender.sendMessage(Prefix.GUILD + "/guild §aally deny§2 [Gilde]");
                sender.sendMessage(Prefix.GUILD + "Lehne einen Verbündungsantrag ab");
            }
        });
    }

    @Subcommand("ally")
    public void ally(Player sender) {
        sender.sendMessage(Prefix.GUILD + "/guild §aally invite§2 [Gilde]");
        sender.sendMessage(Prefix.GUILD + "Stelle einen Verbündungsantrag");
        sender.sendMessage(Prefix.GUILD + "/guild §aally accept§2 [Gilde]");
        sender.sendMessage(Prefix.GUILD + "Nehme einen Verbündungsantrag an");
        sender.sendMessage(Prefix.GUILD + "/guild §aally deny§2 [Gilde]");
        sender.sendMessage(Prefix.GUILD + "Lehne einen Verbündungsantrag ab");
    }

    @Subcommand("create")
    public void create(Player sender, @Single String name) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.createGuild(cloudUser, name))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("rename")
    public void rename(Player sender, @Single String name) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.renameGuild(cloudUser, name))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("changeprefix")
    public void changePrefix(Player sender, @Single String prefix) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.changePrefix(cloudUser, prefix))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("chat")
    @CommandAlias("gc")
    public void chat(Player sender, String message) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> guildChatService.sendMessage(cloudUser.getGuild(), cloudUser, message)).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("bank")
    public void bank(Player sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.showBalance(cloudUser))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("leave")
    public void leave(Player sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.leaveGuild(cloudUser))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("bank deposit")
    public void deposit(Player sender, long amount) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.changeBalance(cloudUser, amount, BalanceAction.DEPOSIT))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("bank withdraw")
    public void withdraw(Player sender, long amount) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.changeBalance(cloudUser, amount, BalanceAction.WITHDRAW))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("sethome")
    public void setHome(Player sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.setHome(cloudUser, sender.getLocation()))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("claim")
    public void claim(Player sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.claimRegion(cloudUser, sender.getLocation()))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("home")
    public void home(Player sender) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        sendResponse(sender, service.tpHome(cloudUser, sender));
    }

    @Subcommand("kick")
    @CommandCompletion("@players @nothing")
    public void kick(Player sender, @Single String target) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        cloudUserManagingService.loadUserByName(target).thenAccept(targetUser -> sendResponse(sender, service.kickMember(targetUser, cloudUser))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("promote")
    @CommandCompletion("@players @nothing")
    public void promote(Player sender, @Single String target) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        cloudUserManagingService.loadUserByName(target).thenAccept(targetUser -> sendResponse(sender, service.promoteMember(targetUser, cloudUser))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("demote")
    @CommandCompletion("@players @nothing")
    public void degrade(Player sender, @Single String target) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        cloudUserManagingService.loadUserByName(target).thenAccept(targetUser -> sendResponse(sender, service.degradeMember(targetUser, cloudUser))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("invites")
    public void invites(Player sender) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        sendResponse(sender, guildInviteService.openInvites(cloudUser));
    }

    @Subcommand("invite")
    @CommandCompletion("@players @nothing")
    public void invite(Player sender, @Single String target) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        IGuild guild = cloudUser.getGuild();
        if (guild == null) {
            sender.sendMessage(Prefix.GUILD + "§cDu bist in keiner Gilde!");
            return;
        }
        cloudUserManagingService.loadUserByName(target).thenAccept(targetUser -> sendResponse(sender, guildInviteService.invite(guild, targetUser, cloudUser))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("accept")
    public void accept(Player sender, @Single String guildName) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        IGuild guild = guildManagingService.getGuildByName(guildName);
        if (guild == null) {
            sender.sendMessage(Prefix.GUILD + "§cEntweder hast du dich verschrieben oder diese Gilde existiert nicht/mehr.");
            return;
        }
        sendResponse(sender, guildInviteService.accept(guild, cloudUser));
    }

    @Subcommand("deny")
    public void deny(Player sender, @Single String guildName) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        IGuild guild = guildManagingService.getGuildByName(guildName);
        if (guild == null) {
            sender.sendMessage(Prefix.GUILD + "§cEntweder hast du dich verschrieben oder diese Gilde existiert nicht/mehr.");
            return;
        }
        sendResponse(sender, guildInviteService.deny(guild, cloudUser));
    }

    @Subcommand("ally invites")
    public void allyInvites(Player sender) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        sendResponse(sender, guildInviteAllyService.openInvites(cloudUser.getGuild()));
    }

    @Subcommand("ally invite")
    public void allyInvite(Player sender, @Single String target) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        IGuild guild = cloudUser.getGuild();
        if (guild == null) {
            sender.sendMessage(Prefix.GUILD + "§cDu bist in keiner Gilde!");
            return;
        }
        IGuild other = guildManagingService.getGuilds().stream().filter(g -> g.getName().equalsIgnoreCase(target)).findAny().orElse(null);
        sendResponse(sender, guildInviteAllyService.invite(guild, other, cloudUser));
    }

    @Subcommand("ally accept")
    public void allyAccept(Player sender, @Single String guildName) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        IGuild guild = cloudUser.getGuild();
        if (guild == null) {
            sender.sendMessage(Prefix.GUILD + "§cDu bist in keiner Gilde!");
            return;
        }
        IGuild other = guildManagingService.getGuilds().stream().filter(g -> g.getName().equalsIgnoreCase(guildName)).findAny().orElse(null);
        sendResponse(sender, guildInviteAllyService.accept(other, guild));
    }

    @Subcommand("ally deny")
    public void allyDeny(Player sender, @Single String guildName) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        IGuild guild = cloudUser.getGuild();
        if (guild == null) {
            sender.sendMessage(Prefix.GUILD + "§cDu bist in keiner Gilde!");
            return;
        }
        IGuild other = guildManagingService.getGuilds().stream().filter(g -> g.getName().equalsIgnoreCase(guildName)).findAny().orElse(null);
        sendResponse(sender, guildInviteAllyService.deny(other, guild));
    }

    private void sendResponse(Player player, GuildContentResponse response) {
        switch (response.state()) {
            case ERROR -> player.sendMessage(Prefix.GUILD + "§cFehler§7: §c" + response.message());
            case SUCCESS -> player.sendMessage(Prefix.GUILD + response.message());
        }
    }

}
