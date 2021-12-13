package de.nebelniek.components.tablistchat.utils;

import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;

import java.util.UUID;

public class NameUtils {

    @Setter
    @Getter
    private static LuckPerms luckPerms;

    public static String getColoredName(UUID uuid, String name) {
        User user = luckPerms.getUserManager().getUser(uuid);
        Group group = luckPerms.getGroupManager().getGroup(
                user.getPrimaryGroup()
        );
        CachedMetaData meta = group.getCachedData().getMetaData(
                luckPerms.getContextManager().getStaticQueryOptions()
        );
        return meta.getPrefix().substring(0, 2).replace("&", "§") + name;
    }

    public static String getPrefix(UUID uuid) {
        User user = luckPerms.getUserManager().getUser(uuid);
        Group group = luckPerms.getGroupManager().getGroup(
                user.getPrimaryGroup()
        );
        CachedMetaData meta = group.getCachedData().getMetaData(
                luckPerms.getContextManager().getStaticQueryOptions()
        );
        return ChatColor.translateAlternateColorCodes('&', meta.getPrefix());
    }

    public static String getPrefix(String groupName) {
        Group group = luckPerms.getGroupManager().getGroup(groupName);
        CachedMetaData meta = group.getCachedData().getMetaData(
                luckPerms.getContextManager().getStaticQueryOptions()
        );
        return ChatColor.translateAlternateColorCodes('&', meta.getPrefix());
    }

}

