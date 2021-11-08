package de.nebelniek.content.guild;

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
import de.nebelniek.database.service.GuildManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuildContentService {

    private final GuildManagingService guildManagingService;
    private final CoinsContentService coinsContentService;

    private final GuildChatService chatService;

    public GuildContentResponse createGuild(ICloudUser creator, String name) {
        if (creator.getGuild() != null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist bereits in einer Gilde!");
        if (creator.getCoins() < Prices.GUILD_CREATE.getPrice())
            return new GuildContentResponse(GuildResponseState.ERROR, "Dazu hast du zu wenig Geld!");
        if (guildManagingService.getGuilds().stream().anyMatch(iGuild -> iGuild.getName().equalsIgnoreCase(name)))
            return new GuildContentResponse(GuildResponseState.ERROR, "Es ist bereits eine Gilde mit diesem Namen vorhanden!");
        coinsContentService.removeCoins(creator, Prices.GUILD_CREATE.getPrice());
        guildManagingService.createGuild(creator, name);
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Deine Gilde §d" + name + "§7  wurde §aerfolgreich§7 erstellt!");
    }

    public GuildContentResponse leaveGuild(ICloudUser cloudUser) {
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
        chatService.sendAnnouncement(cloudUser.getGuild(), cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 hat die Gilde §cverlassen§7!");
        String guildName = cloudUser.getGuild().getColor() + cloudUser.getGuild().getName();
        cloudUser.getGuild().getMember().remove(cloudUser);
        cloudUser.setGuild(null);
        cloudUser.setGuildRole(null);
        cloudUser.saveAsync();
        return new GuildContentResponse(GuildResponseState.SUCCESS, "Du hast die Gilde " + guildName + "§c verlassen§7!");
    }

    public GuildContentResponse joinGuild(ICloudUser cloudUser, IGuild guild) {
        if (cloudUser.getGuild() != null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist noch in einer Gilde!");
        guild.getMember().add(cloudUser);
        cloudUser.setGuild(guild);
        cloudUser.setGuildRole(GuildRole.DEFAULT);
        cloudUser.saveAsync();
        chatService.sendAnnouncement(guild, cloudUser.getGuildRole().getColor() + cloudUser.getLastUserName() + "§7 hat die Gilde §abetreten§7!");
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

    public GuildContentResponse changeBalance(ICloudUser cloudUser, long value, BalanceAction action) {
        IGuild guild = cloudUser.getGuild();
        if (cloudUser.getGuild() == null)
            return new GuildContentResponse(GuildResponseState.ERROR, "Du bist in keiner Gilde!");
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
                " beansprucht§7!");
    }

}
