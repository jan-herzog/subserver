package de.nebelniek.content.guild;

import de.nebelniek.components.combatlog.CombatLogService;
import de.nebelniek.components.spawnprotection.SpawnProtectionService;
import de.nebelniek.components.spawnprotection.WorldSpawns;
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
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import de.nebelniek.components.discord.DiscordGuildChannelService;
import de.nebelniek.inventory.util.ItemColors;
import de.nebelniek.components.scoreboard.ScoreboardManagementService;
import de.nebelniek.components.tablistchat.TablistServiceSubserver;
import de.nebelniek.utils.Prefix;
import de.notecho.inventory.animation.AnimationTypePresets;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuildContentService implements Listener {

    private final GuildManagingService guildManagingService;

    private final CloudUserManagingService cloudUserManagingService;

    private final CoinsContentService coinsContentService;

    private final BukkitConfiguration bukkitConfiguration;

    private final TablistServiceSubserver tablistServiceSubserver;

    private final ScoreboardManagementService scoreboardManagementService;

    private final GuildChatService chatService;

    private final DiscordGuildChannelService discordGuildChannelService;

    private final GuildPrefixNameFilter guildPrefixNameFilter;

    private final SpawnProtectionService spawnProtectionService;

    private final CombatLogService combatLogService;

    public GuildContentResponse createGuild(ICloudUser creator, String name) {
        if (creator.getGuild() != null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist bereits in einer Gilde!");
        if (name.length() > 14)
            return new GuildContentResponse(GuildResponseState.ERROR, "Der Name darf nicht länger als 14 Zeichen sein!");
        if (creator.getCoins() < Prices.GUILD_CREATE.getPrice())
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du zu wenig Geld!");
        if (guildPrefixNameFilter.contains(name))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Name darf nicht gewählt werden.");
        if (guildManagingService.getGuilds().stream().anyMatch(iGuild -> iGuild.getName().equalsIgnoreCase(name)))
            return new GuildContentResponse(GuildResponseState.ERROR, "Es ist bereits eine Gilde mit diesem Namen vorhanden!");
        coinsContentService.removeCoins(creator, Prices.GUILD_CREATE.getPrice());
        creator.setGuildRole(GuildRole.LEADER);
        creator.saveAsync();
        guildManagingService.createGuild(creator, name).thenAccept(guild -> {
            creator.setGuild(guild);
            creator.save();
            discordGuildChannelService.createChannelsIfNotExists(guild);
            scoreboardManagementService.updateProfile(creator);
            scoreboardManagementService.updateGuild(creator);
            tablistServiceSubserver.update();
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
            if (nextLeader == null)
                return deleteGuild(cloudUser);
            nextLeader.setGuildRole(GuildRole.LEADER);
            nextLeader.saveAsync();
            scoreboardManagementService.updateProfile(nextLeader);
            tablistServiceSubserver.update();
            chatService.sendAnnouncement(cloudUser.getGuild(), "Da der " + GuildRole.LEADER.getPrettyName() + "§7 die Gilde §cverlassen§7 hat wurde " + nextLeader.getGuildRole().getColor() + nextLeader.getLastUserName() + "§7 zum neuen " + GuildRole.LEADER.getPrettyName() + "§7!");
        }
        cloudUser.setGuildRole(null);
        cloudUser.getGuild().getMember().remove(cloudUser);
        cloudUser.getGuild().saveAsync();
        cloudUser.setGuild(null);
        cloudUser.saveAsync();
        discordGuildChannelService.updateChannels(guild);
        scoreboardManagementService.updateProfile(cloudUser);
        scoreboardManagementService.updateGuild(cloudUser);
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast die Gilde " + guildName + "§c verlassen§7!");
    }

    public GuildContentResponse deleteGuild(ICloudUser cloudUser) {
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuildRole().isHigherOrEquals(GuildRole.LEADER))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        IGuild guild = cloudUser.getGuild();
        String guildName = guild.getColor() + guild.getName();
        if (guild.getRegion() != null)
            coinsContentService.addCoins(cloudUser, Prices.GUILD_CLAIM_REGION.getPrice());
        chatService.sendAnnouncement(cloudUser.getGuild(), cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§c hat die Gilde " + guildName + "§c gelöscht§7!");
        for (IGuild ally : guild.getAllies()) {
            ally.getAllies().remove(guild);
            chatService.sendAnnouncement(ally, "Die §aVerbündung §7mit " + guildName + " wurde §caufgelöst§7, da sie §4gelöscht§7 wurde.");
            ally.saveAsync();
        }
        guildManagingService.deleteGuild(guild);
        if (guild.getBalance() > 0) {
            coinsContentService.addCoins(cloudUser, cloudUser.getGuild().getBalance());
            Player player = Bukkit.getPlayer(cloudUser.getUuid());
            if (player != null)
                player.sendMessage(Prefix.COINS + "Dir wurden die restlichen §e" + cloudUser.getGuild().getBalance() + " §6Coins §7von deiner Gilde §aüberwiesen§7.");
        }
        for (ICloudUser member : guild.getMember()) {
            member.setGuildRole(null);
            member.setGuild(null);
            member.saveAsync();
        }
        updateGuildMembersScoreboard(guild);
        tablistServiceSubserver.update();
        discordGuildChannelService.disposeGuildChannels(guild);
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast die Gilde " + guildName + " §cgelöscht§7!");
    }

    public GuildContentResponse listGuildMember(ICloudUser cloudUser) {
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        IGuild guild = cloudUser.getGuild();
        StringBuilder stringBuilder = new StringBuilder();
        for (ICloudUser iCloudUser : guild.getMember())
            stringBuilder.append("\n").append("§7 - ").append(iCloudUser.getGuildRole().getColor()).append(iCloudUser.getLastUserName());
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Mitglieder deiner Gilde " + guild.getColor() + guild.getName() + "§7:" + stringBuilder.toString());
    }

    public GuildContentResponse kickMember(ICloudUser cloudUser, ICloudUser kicker) {
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuild().equals(kicker.getGuild()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Spieler ist nicht in deiner Gilde!");
        if (!kicker.getGuildRole().isHigher(cloudUser.getGuildRole()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (cloudUser.equals(kicker))
            return new GuildContentResponse(GuildResponseState.ERROR, "Du kannst dich nicht selber kicken!");
        chatService.sendAnnouncement(cloudUser.getGuild(), cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 wurde von " + kicker.getGuildRole().getColor() + kicker.getLastUserName() + "§7 aus der Gilde §cgeworfen§7!");
        String guildName = cloudUser.getGuild().getColor() + cloudUser.getGuild().getName();
        cloudUser.getGuild().getMember().remove(cloudUser);
        cloudUser.getGuild().saveAsync();
        discordGuildChannelService.updateChannels(cloudUser.getGuild());
        cloudUser.setGuild(null);
        cloudUser.setGuildRole(null);
        scoreboardManagementService.updateProfile(cloudUser);
        scoreboardManagementService.updateGuild(cloudUser);
        tablistServiceSubserver.update();
        cloudUser.saveAsync();
        Player target = Bukkit.getPlayer(cloudUser.getUuid());
        if (target != null)
            target.sendMessage(Prefix.GUILD + "Du wurdest aus der Gilde " + guildName + " §cgeworfen§7!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast §5" + cloudUser.getLastUserName() + "§7 aus der Gilde " + guildName + " §cgeworfen§7!");
    }

    public GuildContentResponse joinGuild(ICloudUser cloudUser, IGuild guild, ICloudUser inviter) {
        if (cloudUser.getGuild() != null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist noch in einer Gilde!");
        guild.getMember().add(cloudUser);
        guild.saveAsync();
        cloudUser.setGuild(guild);
        cloudUser.setGuildRole(GuildRole.DEFAULT);
        scoreboardManagementService.updateProfile(cloudUser);
        scoreboardManagementService.updateGuild(cloudUser);
        discordGuildChannelService.updateChannels(cloudUser.getGuild());
        tablistServiceSubserver.update();
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
        if (guildPrefixNameFilter.contains(name))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Name darf nicht gewählt werden.");
        if (guild.getName().equals(name))
            return new GuildContentResponse(GuildResponseState.ERROR, "Der alte und der neue Name sind identisch!");
        guild.setName(name);
        guild.setBalance(guild.getBalance() - Prices.GUILD_RENAME.getPrice());
        updateGuildMembersScoreboard(guild);
        discordGuildChannelService.updateChannels(guild);
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
        updateGuildMembersScoreboard(guild);
        tablistServiceSubserver.update();
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
        if (guildPrefixNameFilter.contains(prefix))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Prefix darf nicht gewählt werden.");
        if (guildManagingService.getGuilds().stream().anyMatch(g -> g.getPrefix() != null && g.getPrefix().equalsIgnoreCase(prefix)))
            return new GuildContentResponse(GuildResponseState.ERROR, "Der Prefix ist bereits verwendet!");
        guild.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
        guild.setBalance(guild.getBalance() - Prices.GUILD_CHANGE_PREFIX.getPrice());
        guild.saveAsync();
        tablistServiceSubserver.update();
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

    public GuildContentResponse tpHome(ICloudUser cloudUser, Player player, String guildName) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (combatLogService.isInFight(cloudUser))
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist im Fight");
        if (guildName == null && guild.getHome() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Deine Gilde hat keinen Home-Point!");
        if (cloudUser.getCoins() < Prices.GUILD_TP_HOME.getPrice())
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du leider zu wenig Geld!");
        if (guildName != null)
            guild = guildManagingService.getGuilds().stream().filter(g -> g.getName().equalsIgnoreCase(guildName)).findAny().orElse(null);
        if (guild == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Diese Gilde existiert nicht!");
        if (!cloudUser.getGuild().equals(guild) && !guild.getAllies().contains(cloudUser.getGuild()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Mit dieser Gilde bist du nicht verbündet!");
        if (guild.getHome() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Diese Gilde hat keinen Home-Point!");
        coinsContentService.removeCoins(cloudUser, Prices.GUILD_TP_HOME.getPrice());
        IGuild finalGuild = guild;
        Bukkit.getScheduler().runTask(bukkitConfiguration.getPlugin(), () -> player.teleport(
                new Location(
                        Bukkit.getWorld(finalGuild.getHome().world()),
                        finalGuild.getHome().x(),
                        finalGuild.getHome().y(),
                        finalGuild.getHome().z()
                )
        ));
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du wurdest §aerfolgreich §7nach Hause " + (guildName != null ? "§7von " + guild.getColor() + guild.getName() + " " : "") + "§7teleportiert!");
    }

    public GuildContentResponse tpSpawn(ICloudUser cloudUser, Player player) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (guild.getHome() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Deine Gilde hat keinen Home-Point!");
        if (combatLogService.isInFight(cloudUser))
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist im Fight");
        if (cloudUser.getCoins() < Prices.GUILD_TP_HOME.getPrice())
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du leider zu wenig Geld!");
        coinsContentService.removeCoins(cloudUser, Prices.GUILD_TP_HOME.getPrice());
        Bukkit.getScheduler().runTask(bukkitConfiguration.getPlugin(), () -> player.teleport(Bukkit.getWorld("world").getSpawnLocation()));
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du wurdest §aerfolgreich §7zum Spawn teleportiert!");
    }

    public GuildContentResponse changeBalance(ICloudUser cloudUser, long value, BalanceAction action) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuildRole().isHigherOrEquals(guild.getSettings().getManageBankAccountRole()) && action.equals(BalanceAction.WITHDRAW))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
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
        switch (action) {
            case DEPOSIT -> guild.setBalance(guild.getBalance() + value);
            case WITHDRAW -> guild.setBalance(guild.getBalance() - value);
        }
        guild.saveAsync();
        if (action.equals(BalanceAction.DEPOSIT))
            return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast " + ItemColors.BANK.getPrimary() + value + "§7$ an deine" + ItemColors.BANK.getAccent() + " Gilden-Bank §aeingezahlt§7.");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast " + ItemColors.BANK.getPrimary() + value + "§7$ aus deiner" + ItemColors.BANK.getAccent() + " Gilden-Bank §causgezahlt§7.");
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
        if (guild.getBalance() < getPrice(guild.getRegion(), direction))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hat deine Gilde zu wenig Geld!");
        if (guild.getRegion() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Deine Gilde hat noch keine Region beansprucht!");
        Region clone = new Region(guild.getRegion());
        clone.expand(10, direction);
        if (WorldSpawns.getByName(clone.getWorld()) != null)
            if (clone.doesCollide(WorldSpawns.getByName(clone.getWorld()).getRegion()))
                return new GuildContentResponse(GuildResponseState.ERROR, "In diese Richtung ist kein Platz für deine Gilde!");
        for (IGuild iGuild : guildManagingService.getGuilds())
            if (guild != iGuild)
                if (iGuild.getRegion() != null)
                    if (iGuild.getRegion().doesCollide(clone))
                        return new GuildContentResponse(GuildResponseState.ERROR, "In diese Richtung ist kein Platz für deine Gilde!");
        guild.getRegion().expand(10, direction);
        guild.setBalance(guild.getBalance() - getPrice(guild.getRegion(), direction));
        guild.saveAsync();
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast §aerfolgreich §7die Region deiner Gilde nach §e" + direction + " §aerweitert§7!");
    }

    @SneakyThrows
    public GuildContentResponse disposeRegion(ICloudUser cloudUser) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuildRole().isHigherOrEquals(guild.getSettings().getManageRegionRole()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (guild.getRegion() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Deine Gilde hat noch keine Region beansprucht!");
        guildManagingService.getDatabaseProvider().getRegionDao().delete(guild.getRegion().getModel());
        guild.setRegion(null);
        guild.saveAsync();
        coinsContentService.addCoins(cloudUser, Prices.GUILD_CLAIM_REGION.getPrice());
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast §aerfolgreich §7die Region deiner Gilde §cgelöscht§7! Dir wurden §e" + Prices.GUILD_CLAIM_REGION.getPrice() + "§7 gutgeschrieben.");
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
        if (spawnProtectionService.isNearSpawn(location))
            return new GuildContentResponse(GuildResponseState.ERROR, "Entferne dich noch etwas vom Spawn!");
        if (WorldSpawns.getByLocation(location) != null)
            if (WorldSpawns.getByLocation(location).getRegion().doesCollide(location.getWorld().getName(), location.getX() - 20, location.getZ() - 20, location.getX() + 20, location.getZ() + 20))
                return new GuildContentResponse(GuildResponseState.ERROR, "Hier ist leider kein Platz für deine Gilde!");
        for (IGuild iGuild : guildManagingService.getGuilds())
            if (iGuild.getRegion() != null)
                if (iGuild.getRegion().doesCollide(location.getWorld().getName(), location.getX() - 20, location.getZ() - 20, location.getX() + 20, location.getZ() + 20))
                    return new GuildContentResponse(GuildResponseState.ERROR, "Hier ist leider kein Platz für deine Gilde!");
        guildManagingService.createRegion(location.getWorld().getName(), location.getX() - 20, location.getZ() - 20, location.getX() + 20, location.getZ() + 20).thenAccept(region -> {
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
        if (cloudUser.getGuildRole().equals(GuildRole.LEADER))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Spieler kann nicht weiter befördert werden!");
        if (!promoter.getGuildRole().isHigher(cloudUser.getGuildRole()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (cloudUser.equals(promoter))
            return new GuildContentResponse(GuildResponseState.ERROR, "Du kannst dich nicht selber promoten!");
        if (cloudUser.getGuildRole().equals(GuildRole.ADMIN)) {
            chatService.sendAnnouncement(cloudUser.getGuild(), promoter.getGuildRole().getColor() + promoter.getLastUserName() + "§7 hat seinen Posten als " + promoter.getGuildRole().getPrettyName() + " §cabgegeben§7 und ist nun " + promoter.getGuildRole().oneDown().getPrettyName() + "§7!");
            promoter.setGuildRole(promoter.getGuildRole().oneDown());
            promoter.saveAsync();
            scoreboardManagementService.updateProfile(promoter);
        }
        cloudUser.setGuildRole(cloudUser.getGuildRole().oneUp());
        cloudUser.saveAsync();
        scoreboardManagementService.updateProfile(cloudUser);
        chatService.sendAnnouncement(cloudUser.getGuild(), cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 wurde von " + promoter.getGuildRole().getColor() + promoter.getLastUserName() + "§7 zum " + cloudUser.getGuildRole().getPrettyName() + " §abefördert§7!");
        Player player = Bukkit.getPlayer(cloudUser.getUuid());
        if (player != null)
            player.sendMessage(Prefix.GUILD + "Du wurdest zum " + cloudUser.getGuildRole().getPrettyName() + " §abefördert§7!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast " + cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 zum " + cloudUser.getGuildRole().getPrettyName() + " §abefördert§7!");
    }

    public GuildContentResponse degradeMember(ICloudUser cloudUser, ICloudUser degrader) {
        if (degrader.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        if (!cloudUser.getGuild().equals(degrader.getGuild()))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Spieler ist nicht in deiner Gilde!");
        if (!degrader.getGuildRole().isHigher(cloudUser.getGuildRole()) && !cloudUser.equals(degrader))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du keine Rechte!");
        if (cloudUser.getGuildRole().equals(GuildRole.DEFAULT))
            return new GuildContentResponse(GuildResponseState.ERROR, "Dieser Spieler kann nicht weiter degradiert werden!");
        if (degrader.equals(cloudUser) && degrader.getGuildRole().equals(GuildRole.LEADER)) {
            ICloudUser nextLeader = getNextLeader(cloudUser.getGuild());
            if (nextLeader == null)
                return new GuildContentResponse(GuildResponseState.ERROR, "Deine Gilde hat kein Mitglied, was deine Position einehmen könnte!");
            chatService.sendAnnouncement(cloudUser.getGuild(), "Da der " + GuildRole.LEADER.getPrettyName() + "§7 seinen Posten §cabgegeben§7 hat wurde " + nextLeader.getGuildRole().getColor() + nextLeader.getLastUserName() + "§7 zum neuen " + GuildRole.LEADER.getPrettyName() + "§7!");
            nextLeader.setGuildRole(GuildRole.LEADER);
            nextLeader.saveAsync();
            scoreboardManagementService.updateProfile(nextLeader);
        }
        cloudUser.setGuildRole(cloudUser.getGuildRole().oneDown());
        cloudUser.saveAsync();
        scoreboardManagementService.updateProfile(cloudUser);
        chatService.sendAnnouncement(cloudUser.getGuild(), cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 wurde von " + degrader.getGuildRole().getColor() + degrader.getLastUserName() + " §7zum " + cloudUser.getGuildRole().getPrettyName() + " §cdegradiert§7!");
        Player player = Bukkit.getPlayer(cloudUser.getUuid());
        if (player != null)
            player.sendMessage(Prefix.GUILD + "Du wurdest zum " + cloudUser.getGuildRole().getPrettyName() + " §cdegradiert§7!");
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast " + cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + " zum " + cloudUser.getGuildRole().getPrettyName() + " §cdegradiert§7!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> {
            if (cloudUser.getGuild() == null)
                return;
            chatService.sendAnnouncement(cloudUser.getGuild(), cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 ist nun §aonline§7!");
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cloudUserManagingService.loadUser(event.getPlayer().getUniqueId()).thenAccept(cloudUser -> {
            if (cloudUser.getGuild() == null)
                return;
            chatService.sendAnnouncement(cloudUser.getGuild(), cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 ist nun §coffline§7!");
        });
    }

    public long getPrice(IRegion region, Direction direction) {
        return (long) Prices.GUILD_EXPAND_REGION_BLOCK.getPrice() * direction.getBlocks(region, 10);
    }


    private void updateGuildMembersScoreboard(IGuild guild) {
        for (ICloudUser cloudUser : guild.getMember()) {
            Player player = Bukkit.getPlayer(cloudUser.getUuid());
            if (player != null) {
                scoreboardManagementService.updateGuild(cloudUser);
                scoreboardManagementService.updateProfile(cloudUser);
            }
        }
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
