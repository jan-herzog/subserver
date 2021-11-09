package de.nebelniek.content.guild;

import de.nebelniek.configuration.BukkitConfiguration;
import de.nebelniek.configuration.Prices;
import de.nebelniek.content.coins.CoinsContentService;
import de.nebelniek.content.guild.chat.GuildChatService;
import de.nebelniek.content.guild.response.GuildContentResponse;
import de.nebelniek.content.guild.response.GuildResponseState;
import de.nebelniek.database.guild.Region;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.guild.interfaces.IRegion;
import de.nebelniek.database.guild.util.Direction;
import de.nebelniek.database.guild.util.GuildRole;
import de.nebelniek.database.guild.util.HomePoint;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.scoreboard.ScoreboardManagementService;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuildContentService {

    private final GuildManagingService guildManagingService;

    private final CoinsContentService coinsContentService;

    private final BukkitConfiguration bukkitConfiguration;

    private final ScoreboardManagementService scoreboardManagementService;

    private final GuildChatService chatService;

    public GuildContentResponse createGuild(ICloudUser creator, String name) {
        if (creator.getGuild() != null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist bereits in einer Gilde!");
        if (creator.getCoins() < Prices.GUILD_CREATE.getPrice())
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du zu wenig Geld!");
        if (guildManagingService.getGuilds().stream().anyMatch(iGuild -> iGuild.getName().equalsIgnoreCase(name)))
            return new GuildContentResponse(GuildResponseState.ERROR, "Es ist bereits eine Gilde mit diesem Namen vorhanden!");
        coinsContentService.removeCoins(creator, Prices.GUILD_CREATE.getPrice());
        guildManagingService.createGuild(creator, name).thenAccept(guild -> {
            creator.setGuild(guild);
            creator.setGuildRole(GuildRole.LEADER);
            scoreboardManagementService.updateProfile(creator);
            scoreboardManagementService.updateGuild(creator);
            creator.saveAsync();
        });
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Deine Gilde §d" + name + "§7 wurde §aerfolgreich§7 erstellt!");
    }

    public GuildContentResponse leaveGuild(ICloudUser cloudUser) {
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        chatService.sendAnnouncement(cloudUser.getGuild(), cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 hat die Gilde §cverlassen§7!");
        String guildName = cloudUser.getGuild().getColor() + cloudUser.getGuild().getName();
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuildRole().isHigherOrEquals(GuildRole.LEADER)) {
            ICloudUser nextLeader = getNextLeader(guild);
            if(nextLeader == null) {
                chatService.sendAnnouncement(cloudUser.getGuild(), "§cDa das letzte Mitglied die Gilde verlassen hat wurde sie gelöscht§7!");
                guildManagingService.deleteGuild(guild);
                return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast die Gilde " + guildName + " §cgelöscht§7!");
            }
            nextLeader.setGuildRole(GuildRole.LEADER);
            chatService.sendAnnouncement(cloudUser.getGuild(), "Da der " + GuildRole.LEADER.getPrettyName() + " die Gilde §cverlassen§7 hat wurde " + nextLeader.getGuildRole().getColor() + nextLeader.getLastUserName() + "§7 zum neuen " + GuildRole.LEADER.getPrettyName() + "§7!");
            cloudUser.setGuildRole(null);
            cloudUser.getGuild().getMember().remove(cloudUser);
            cloudUser.setGuild(null);
            scoreboardManagementService.updateProfile(cloudUser);
            scoreboardManagementService.updateGuild(cloudUser);
            cloudUser.saveAsync();
        }
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast die Gilde " + guildName + "§c verlassen§7!");
    }

    public GuildContentResponse kickMember(ICloudUser cloudUser, ICloudUser kicker) {
        if (kicker.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuild().equals(kicker.getGuild()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Spieler ist nicht in deiner Gilde!");
        if (!kicker.getGuildRole().isHigher(cloudUser.getGuildRole()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (cloudUser.equals(kicker))
            return new GuildContentResponse(GuildResponseState.ERROR, "Du kannst dich nicht selber kicken!");
        chatService.sendAnnouncement(cloudUser.getGuild(), cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 wurde von " + kicker.getGuildRole().getColor() + kicker.getLastUserName() + " aus der Gilde §cgeworfen§7!");
        String guildName = cloudUser.getGuild().getColor() + cloudUser.getGuild().getName();
        cloudUser.getGuild().getMember().remove(cloudUser);
        cloudUser.setGuild(null);
        cloudUser.setGuildRole(null);
        cloudUser.saveAsync();
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du wurdest aus der Gilde " + guildName + " §cgeworfen§7!");
    }

    public GuildContentResponse joinGuild(ICloudUser cloudUser, IGuild guild, ICloudUser inviter) {
        if (cloudUser.getGuild() != null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist noch in einer Gilde!");
        guild.getMember().add(cloudUser);
        cloudUser.setGuild(guild);
        cloudUser.setGuildRole(GuildRole.DEFAULT);
        scoreboardManagementService.updateProfile(cloudUser);
        scoreboardManagementService.updateGuild(cloudUser);
        cloudUser.saveAsync();
        chatService.sendAnnouncement(guild, cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 hat die Gilde §abetreten§7! (Eingeladen von " + inviter.getGuildRole().getColor() + inviter.getLastUserName() + "§7)");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast die Gilde " + guild.getColor() + guild.getName() + "§a betreten§7!");
    }

    public GuildContentResponse renameGuild(ICloudUser cloudUser, String name) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuildRole().isHigherOrEquals(GuildRole.ADMIN))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (guild.getBalance() < Prices.GUILD_RENAME.getPrice())
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hat deine Gilde zu wenig Geld!");
        if (guild.getName().equals(name))
            return new GuildContentResponse(GuildResponseState.ERROR, "Der alte und der neue Name sind identisch!");
        guild.setName(name);
        guild.setBalance(guild.getBalance() - Prices.GUILD_RENAME.getPrice());
        scoreboardManagementService.updateGuild(cloudUser);
        guild.saveAsync();
        chatService.sendAnnouncement(guild, cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 hat den Namen der Gilde zu " + guild.getColor() + guild.getName() + "§7 geändert!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast den Namen §aerfolgreich §7geändert!");
    }

    public GuildContentResponse changeColor(ICloudUser cloudUser, String color) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuildRole().isHigherOrEquals(GuildRole.ADMIN))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (guild.getBalance() < Prices.GUILD_CHANGE_COLOR.getPrice())
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hat deine Gilde zu wenig Geld!");
        if (guild.getColor().equals(color))
            return new GuildContentResponse(GuildResponseState.ERROR, "Die alte und die neue Farbe sind identisch!");
        guild.setColor(color);
        guild.setBalance(guild.getBalance() - Prices.GUILD_CHANGE_COLOR.getPrice());
        scoreboardManagementService.updateGuild(cloudUser);
        guild.saveAsync();
        chatService.sendAnnouncement(guild, cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 hat die Farbe der Gilde zu " + guild.getColor() + guild.getName() + "§7 geändert!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast die Farbe §aerfolgreich §7geändert!");
    }

    public GuildContentResponse changePrefix(ICloudUser cloudUser, String prefix) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuildRole().isHigherOrEquals(GuildRole.ADMIN))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (guild.getBalance() < Prices.GUILD_CHANGE_PREFIX.getPrice())
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hat deine Gilde zu wenig Geld!");
        if (guild.getPrefix() != null)
            if (guild.getPrefix().equals(prefix))
                return new GuildContentResponse(GuildResponseState.ERROR, "Der alte und der neue Prefix sind identisch!");
        if (prefix.length() > 16)
            return new GuildContentResponse(GuildResponseState.ERROR, "Der Prefix darf nicht länger als 16 Zeichen sein!");
        guild.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
        guild.setBalance(guild.getBalance() - Prices.GUILD_CHANGE_PREFIX.getPrice());
        guild.saveAsync();
        chatService.sendAnnouncement(guild, cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 hat den Prefix der Gilde zu " + guild.getPrefix() + "§7 geändert!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast den Prefix §aerfolgreich §7geändert!");
    }

    public GuildContentResponse setHome(ICloudUser cloudUser, Location location) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuildRole().isHigherOrEquals(GuildRole.ADMIN))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (guild.getBalance() < Prices.GUILD_SET_HOME.getPrice())
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hat deine Gilde zu wenig Geld!");
        guild.setHome(new HomePoint(location.getWorld().getName(), location.getX(), location.getY(), location.getZ()));
        guild.setBalance(guild.getBalance() - Prices.GUILD_SET_HOME.getPrice());
        guild.saveAsync();
        chatService.sendAnnouncement(guild, cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 hat den Homepunkt der Gilde zu §e" +
                ((int) location.getX()) + "§7, §e" +
                ((int) location.getY()) + "§7, §e" +
                ((int) location.getZ()) + "§7, §e"
                + "§7 geändert!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast den Homepunkt §aerfolgreich §7geändert!");
    }

    public GuildContentResponse tpHome(ICloudUser cloudUser, Player player) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (cloudUser.getCoins() < 500)
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du leider zu wenig Geld!");
        coinsContentService.removeCoins(cloudUser, 500);
        Bukkit.getScheduler().runTask(bukkitConfiguration.getPlugin(), () -> player.teleport(
                new Location(
                        Bukkit.getWorld(guild.getHome().world()),
                        guild.getHome().x(),
                        guild.getHome().y(),
                        guild.getHome().z()
                )
        ));
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du wurdest §aerfolgreich §7nach Hause teleportiert!");
    }

    public GuildContentResponse changeBalance(ICloudUser cloudUser, long value, BalanceAction action) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        switch (action) {
            case DEPOSIT -> {
                if (cloudUser.getCoins() < value)
                    return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du zu wenig Geld!");
                coinsContentService.removeCoins(cloudUser, value);
            }
            case WITHDRAW -> {
                if (cloudUser.getGuild().getBalance() < value)
                    return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hat deine Gilde zu wenig Geld!");
                coinsContentService.addCoins(cloudUser, value);
            }
        }
        if (!cloudUser.getGuildRole().isHigherOrEquals(guild.getSettings().getManageBankAccountRole()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        switch (action) {
            case DEPOSIT -> guild.setBalance(guild.getBalance() + value);
            case WITHDRAW -> guild.setBalance(guild.getBalance() - value);
        }
        guild.saveAsync();
        if (action.equals(BalanceAction.DEPOSIT))
            return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast erfolgreich §e" + value + "§a eingezahlt§7!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast erfolgreich §e" + value + "§c ausgezahlt§7!");
    }

    public GuildContentResponse showBalance(ICloudUser cloudUser) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Deine Gilde hat §e" + guild.getBalance() + "§7$ auf dem Konto!");
    }

    public GuildContentResponse expandRegion(ICloudUser cloudUser, Direction direction) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuildRole().isHigherOrEquals(guild.getSettings().getManageRegionRole()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (guild.getBalance() < Prices.GUILD_EXPAND_REGION.getPrice())
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hat deine Gilde zu wenig Geld!");
        if (guild.getRegion() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Deine Gilde hat noch keine Region beansprucht!");
        Region clone = new Region(guild.getRegion());
        clone.expand(10, direction);
        for (IGuild iGuild : guildManagingService.getGuilds())
            if (iGuild.getRegion().doesCollide(clone))
                return new GuildContentResponse(GuildResponseState.ERROR, "In diese Richtung ist kein Platz für deine Gilde!");
        guild.getRegion().expand(10, direction);
        guild.setBalance(guild.getBalance() - Prices.GUILD_EXPAND_REGION.getPrice());
        guild.saveAsync();
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast erfolgreich  ausgezahlt§7!");
    }

    public GuildContentResponse claimRegion(ICloudUser cloudUser, Location location) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuildRole().isHigherOrEquals(guild.getSettings().getManageRegionRole()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (guild.getBalance() < Prices.GUILD_CLAIM_REGION.getPrice())
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hat deine Gilde zu wenig Geld!");
        if (guild.getRegion() != null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Deine Gilde hat bereits eine Region beansprucht!");
        guildManagingService.createRegion(location.getX() - 20, location.getZ() - 20, location.getX() + 20, location.getZ() + 20).thenAccept(region -> {
            guild.setBalance(guild.getBalance() - Prices.GUILD_CLAIM_REGION.getPrice());
            guild.setRegion(region);
            guild.saveAsync();
        });
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast erfolgreich die Region §7(" +
                "§e" + ((int) (location.getX() - 20)) + "§7, §e" + ((int) (location.getZ() - 20)) + "§7 - §e" +
                "§e" + ((int) (location.getX() + 20)) + "§7, §e" + ((int) (location.getZ() + 20)) +
                "§7) beansprucht§7!");
    }

    public GuildContentResponse promoteMember(ICloudUser cloudUser, ICloudUser promoter) {
        if (promoter.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuild().equals(promoter.getGuild()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Spieler ist nicht in deiner Gilde!");
        if (!promoter.getGuildRole().isHigher(cloudUser.getGuildRole()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (cloudUser.equals(promoter))
            return new GuildContentResponse(GuildResponseState.ERROR, "Du kannst dich nicht selber promoten!");
        if (cloudUser.getGuildRole().equals(GuildRole.ADMIN)) {
            chatService.sendAnnouncement(cloudUser.getGuild(), promoter.getGuildRole().getColor() + promoter.getLastUserName() + "§7 hat seinen Posten als " + promoter.getGuildRole().getPrettyName() + " §cabgegeben und ist nun " + promoter.getGuildRole().oneDown().getPrettyName() + "§7!");
            promoter.setGuildRole(promoter.getGuildRole().oneDown());
        }
        cloudUser.setGuildRole(cloudUser.getGuildRole().oneUp());
        cloudUser.saveAsync();
        chatService.sendAnnouncement(cloudUser.getGuild(), cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 wurde von " + promoter.getGuildRole().getColor() + promoter.getLastUserName() + " aus der Gilde zum " + cloudUser.getGuildRole().getPrettyName() + " §abefördert§7!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du wurdest zum " + cloudUser.getGuildRole().getPrettyName() + " §abefördert§7!");
    }

    public GuildContentResponse degradeMember(ICloudUser cloudUser, ICloudUser degrader) {
        if (degrader.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuild().equals(degrader.getGuild()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Spieler ist nicht in deiner Gilde!");
        if (!degrader.getGuildRole().isHigher(cloudUser.getGuildRole()) && !cloudUser.equals(degrader))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (!cloudUser.getGuildRole().equals(GuildRole.DEFAULT))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Spieler kann nicht weiter degradiert werden!");
        if(degrader.equals(cloudUser) && degrader.getGuildRole().equals(GuildRole.LEADER)) {
            ICloudUser nextLeader = getNextLeader(cloudUser.getGuild());
            if(nextLeader == null)
                return new GuildContentResponse(GuildResponseState.ERROR, "Deine Gilde hat kein Mitglied, was deine Position einehmen könnte!");
            chatService.sendAnnouncement(cloudUser.getGuild(), "Da der " + GuildRole.LEADER.getPrettyName() + " seinen Posten §cabgegeben§7 hat wurde " + nextLeader.getGuildRole().getColor() + nextLeader.getLastUserName() + "§7 zum neuen " + GuildRole.LEADER.getPrettyName() + "§7!");
        }
        cloudUser.setGuildRole(cloudUser.getGuildRole().oneDown());
        cloudUser.saveAsync();
        chatService.sendAnnouncement(cloudUser.getGuild(), cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 wurde von " + degrader.getGuildRole().getColor() + degrader.getLastUserName() + " aus der Gilde zum " + cloudUser.getGuildRole().getPrettyName() + " §cdegradiert§7!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du wurdest zum " + cloudUser.getGuildRole().getPrettyName() + " §cdegradiert§7!");
    }


    private ICloudUser getNextLeader(IGuild guild) {
        for (GuildRole value : GuildRole.values()) {
            if (value.equals(GuildRole.LEADER))
                continue;
            for (ICloudUser iCloudUser : guild.getMember())
                if (iCloudUser.getGuildRole().equals(value))
                    return iCloudUser;
        }
        return null;
    }

}
