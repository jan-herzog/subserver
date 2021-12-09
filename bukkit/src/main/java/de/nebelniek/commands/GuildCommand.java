package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.nebelniek.configuration.BukkitConfiguration;
import de.nebelniek.configuration.Prices;
import de.nebelniek.content.guild.BalanceAction;
import de.nebelniek.content.guild.GuildContentService;
import de.nebelniek.content.guild.chat.GuildChatService;
import de.nebelniek.content.guild.invite.GuildInviteService;
import de.nebelniek.content.guild.invite.ally.GuildInviteAllyService;
import de.nebelniek.content.guild.response.GuildContentResponse;
import de.nebelniek.content.guild.response.GuildResponseState;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.util.GuildRole;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.inventory.guild.GuildMainMenu;
import de.nebelniek.inventory.guild.NoGuildMenu;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
                Bukkit.getScheduler().runTask(configuration.getPlugin(), () -> new GuildMainMenu(cloudUser.getGuild(), cloudUser).open(sender));
                return;
            }
            Bukkit.getScheduler().runTask(configuration.getPlugin(), () -> new NoGuildMenu(cloudUser).open(sender));
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("help")
    @CatchUnknown
    public void help(Player sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> {
            sender.sendMessage(Prefix.GUILD + "§l§6Hilfe§7 für §a/guild§7:");
            sender.sendMessage(Prefix.GUILD + "/guild");
            sender.sendMessage(Prefix.GUILD + "Öffnet das Guild-Menu");
            sender.sendMessage(Prefix.GUILD + "/guild §acreate§2 [name]");
            sender.sendMessage(Prefix.GUILD + "Erstellt eine Gilde | Kosten: §e50k");
            sender.sendMessage(Prefix.GUILD + "/guild §aleave§2 [name]");
            sender.sendMessage(Prefix.GUILD + "Verlasse deine Gilde");
            sender.sendMessage(Prefix.GUILD + "/guild §adelete§2 [name]");
            sender.sendMessage(Prefix.GUILD + "Lösche deine Gilde");
            sender.sendMessage(Prefix.GUILD + "/guild §ainvite§2 [Spieler]");
            sender.sendMessage(Prefix.GUILD + "Lade einen Spieler in deine Gilde ein");
            sender.sendMessage(Prefix.GUILD + "/guild §aaccept§2 [Gilde]");
            sender.sendMessage(Prefix.GUILD + "Nehme eine Einladung an");
            sender.sendMessage(Prefix.GUILD + "/guild §adeny§2 [Gilde]");
            sender.sendMessage(Prefix.GUILD + "Lehne eine Einladung ab");
            if (cloudUser.getGuild() != null) {
                sender.sendMessage(Prefix.GUILD + "/guild §arename§2 [name]");
                sender.sendMessage(Prefix.GUILD + "Nenne deine Gilde um | Kosten: §e10k");
                sender.sendMessage(Prefix.GUILD + "/guild §achangeprefix§2 [prefix]");
                sender.sendMessage(Prefix.GUILD + "Setzt den Prefix | Kosten: §e15k");
                sender.sendMessage(Prefix.GUILD + "/guild §achangecolor§2 [colorcode]");
                sender.sendMessage(Prefix.GUILD + "Setzt die Farbe | Kosten: §e5k");
                sender.sendMessage(Prefix.GUILD + "/guild §achat§2 [Nachricht]");
                sender.sendMessage(Prefix.GUILD + "Alias: §a/gc");
                sender.sendMessage(Prefix.GUILD + "Sende deinen Kollegen eine Nachricht");
                sender.sendMessage(Prefix.GUILD + "/guild §abank");
                sender.sendMessage(Prefix.GUILD + "/guild §abank deposit§2 [Wert]");
                sender.sendMessage(Prefix.GUILD + "/guild §abank withdraw§2 [Wert]");
                sender.sendMessage(Prefix.GUILD + "Benutze das Gilden-Konto");
                sender.sendMessage(Prefix.GUILD + "/guild §asethome");
                sender.sendMessage(Prefix.GUILD + "Setzte deiner Gilde einen Homepunkt | Kosten: §e5k");
                sender.sendMessage(Prefix.GUILD + "/guild §ahome");
                sender.sendMessage(Prefix.GUILD + "Teleportiert dich nach Hause | Kosten: §e" + Prices.GUILD_TP_HOME.getPrice());
                sender.sendMessage(Prefix.GUILD + "/guild §alist");
                sender.sendMessage(Prefix.GUILD + "Zeigt die Mitglieder deiner Gilde");
                sender.sendMessage(Prefix.GUILD + "/guild §aclaim");
                sender.sendMessage(Prefix.GUILD + "Beanspruche eine Region für deine Gilde | Kosten: §e20k");
                sender.sendMessage(Prefix.GUILD + "/guild §akick§2 [Spieler]");
                sender.sendMessage(Prefix.GUILD + "Kicke ein Mitglied");
                ally(sender);
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
        sender.sendMessage(Prefix.GUILD + "/guild §aally disconnect§2 [Gilde]");
        sender.sendMessage(Prefix.GUILD + "Trenne dich von einer Gilde");
        sender.sendMessage(Prefix.GUILD + "/guild §aally list");
        sender.sendMessage(Prefix.GUILD + "Sehe mit wem du Verbündet bist.");
    }

    @Subcommand("create")
    @Syntax("§7[§ename§7]")
    public void create(Player sender, @Single String name) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.createGuild(cloudUser, name))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("rename")
    @Syntax("§7[§ename§7]")
    public void rename(Player sender, @Single String name) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.renameGuild(cloudUser, name))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("changeprefix")
    @Syntax("§7[§eprefix§7]")
    public void changePrefix(Player sender, String text) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.changePrefix(cloudUser, text))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("changecolor")
    @Syntax("§7[§ecolorcode§7]")
    @CommandCompletion("1|2|3|4|5|6|7|8|9|0|a|b|c|d|e")
    public void changeColor(Player sender, @Single String colorcode) {
        ChatColor chatColor = ChatColor.getByChar(colorcode.charAt(0));
        if (chatColor == null || !chatColor.isColor()) {
            sender.sendMessage(
                    net.kyori.adventure.text.Component.text(Prefix.GUILD + "§cDies ist kein valider §aColorcode§c!§7 Eine Liste mit validen Codes findest du im Wiki: ")
                            .append(
                                    net.kyori.adventure.text.Component.text("§ahttps://subserver.nebelniek.de/wiki§7.")
                                            .clickEvent(ClickEvent.openUrl("https://subserver.nebelniek.de/wiki"))
                                            .hoverEvent(net.kyori.adventure.text.Component.text("§a§lKlick!").asHoverEvent())
                            )
            );
            return;
        }
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.changeColor(cloudUser, "§" + colorcode))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("chat")
    @CommandAlias("gc")
    @Syntax("§7[§emessage§7]")
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

    @Subcommand("list")
    public void list(Player sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.listGuildMember(cloudUser))).exceptionally(throwable -> {
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

    @Subcommand("delete")
    public void delete(Player sender) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.deleteGuild(cloudUser))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("bank deposit")
    @Syntax("§7[§eamount§7]")
    public void deposit(Player sender, long amount) {
        cloudUserManagingService.loadUser(sender.getUniqueId()).thenAccept(cloudUser -> sendResponse(sender, service.changeBalance(cloudUser, amount, BalanceAction.DEPOSIT))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("bank withdraw")
    @Syntax("§7[§eamount§7]")
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
    @Syntax("§7[§eplayer§7]")
    public void kick(Player sender, @Single String target) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        cloudUserManagingService.loadUserByName(target).thenAccept(targetUser -> sendResponse(sender, service.kickMember(targetUser, cloudUser))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("promote")
    @CommandCompletion("@players @nothing")
    @Syntax("§7[§eplayer§7]")
    public void promote(Player sender, @Single String target) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        cloudUserManagingService.loadUserByName(target).thenAccept(targetUser -> sendResponse(sender, service.promoteMember(targetUser, cloudUser))).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Subcommand("demote")
    @CommandCompletion("@players @nothing")
    @Syntax("§7[§eplayer§7]")
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
    @Syntax("§7[§eplayer§7]")
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
    @Syntax("§7[§eguild§7]")
    public void accept(Player sender, @Single String guildName) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        IGuild guild = guildManagingService.getGuildContainsName(guildName);
        if (guild == null) {
            sender.sendMessage(Prefix.GUILD + "§cEntweder hast du dich verschrieben oder diese Gilde existiert nicht/mehr.");
            return;
        }
        sendResponse(sender, guildInviteService.accept(guild, cloudUser));
    }

    @Subcommand("deny")
    @Syntax("§7[§eguild§7]")
    public void deny(Player sender, @Single String guildName) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        IGuild guild = guildManagingService.getGuildContainsName(guildName);
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
    @Syntax("§7[§eguild§7]")
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
    @Syntax("§7[§eguild§7]")
    public void allyAccept(Player sender, @Single String guildName) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        IGuild guild = cloudUser.getGuild();
        if (guild == null) {
            sender.sendMessage(Prefix.GUILD + "§cDu bist in keiner Gilde!");
            return;
        }
        if (!cloudUser.getGuildRole().isHigherOrEquals(GuildRole.ADMIN)) {
            sender.sendMessage(Prefix.GUILD + "§cDazu hast du keine Rechte!");
            return;
        }
        IGuild other = guildManagingService.getGuilds().stream().filter(g -> g.getName().equalsIgnoreCase(guildName)).findAny().orElse(null);
        sendResponse(sender, guildInviteAllyService.accept(other, guild));
    }

    @Subcommand("ally deny")
    @Syntax("§7[§eguild§7]")
    public void allyDeny(Player sender, @Single String guildName) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        IGuild guild = cloudUser.getGuild();
        if (guild == null) {
            sender.sendMessage(Prefix.GUILD + "§cDu bist in keiner Gilde!");
            return;
        }
        if (!cloudUser.getGuildRole().isHigherOrEquals(GuildRole.ADMIN)) {
            sender.sendMessage(Prefix.GUILD + "§cDazu hast du keine Rechte!");
            return;
        }
        IGuild other = guildManagingService.getGuilds().stream().filter(g -> g.getName().equalsIgnoreCase(guildName)).findAny().orElse(null);
        sendResponse(sender, guildInviteAllyService.deny(other, guild));
    }

    @Subcommand("ally disconnect")
    @Syntax("§7[§eguild§7]")
    public void allyDisconnect(Player sender, @Single String guildName) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        IGuild other = guildManagingService.getGuilds().stream().filter(g -> g.getName().equalsIgnoreCase(guildName)).findAny().orElse(null);
        sendResponse(sender, guildInviteAllyService.disconnectAlly(cloudUser, other));
    }

    @Subcommand("ally list")
    public void allyList(Player sender) {
        ICloudUser cloudUser = cloudUserManagingService.getCloudUsers().get(sender.getUniqueId());
        IGuild guild = cloudUser.getGuild();
        if (guild == null) {
            sender.sendMessage(Prefix.GUILD + "§cDu bist in keiner Gilde!");
            return;
        }
        if (guild.getAllies().size() == 0) {
            sender.sendMessage(Prefix.GUILD + "Du hast §ckeine §aVerbündeten§7!");
            return;
        }
        sender.sendMessage(Prefix.GUILD + "Deine §aVerbündeten§7:");
        for (IGuild ally : guild.getAllies())
            sender.sendMessage(Prefix.GUILD + " - " + ally.getColor() + ally.getName());
    }

    private void sendResponse(Player player, GuildContentResponse response) {
        switch (response.state()) {
            case ERROR -> player.sendMessage(Prefix.GUILD + "§cFehler§7: §c" + response.message());
            case SUCCESS -> player.sendMessage(Prefix.GUILD + response.message());
        }
    }

}
