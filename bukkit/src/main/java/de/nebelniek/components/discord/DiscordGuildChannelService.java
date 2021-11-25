package de.nebelniek.components.discord;

import de.nebelniek.configuration.BukkitConfiguration;
import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.service.CloudUserManagingService;
import de.nebelniek.database.user.interfaces.ICloudUser;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DiscordGuildChannelService {

    private final CloudUserManagingService cloudUserManagingService;

    private final JDA jda;

    public boolean createChannelsIfNotExists(IGuild guild) {
        Guild subserverGuild = BukkitConfiguration.getMainGuild();
        String categoryName = "- " + guild.getName() + " -";
        List<Category> categoryList = subserverGuild.getCategoriesByName(categoryName, true);
        if (categoryList.size() != 0) {
            updateChannels(guild);
            return false;
        }
        subserverGuild.createRole().setName(guild.getName()).queue(role -> {
            guild.setDiscordRole(role.getId());
            guild.saveAsync();
            for (ICloudUser cloudUser : guild.getMember()) {
                if (cloudUser.getDiscordId() == null)
                    continue;
                Member member = subserverGuild.getMemberById(cloudUser.getDiscordId());
                if (member == null) {
                    cloudUser.setDiscordId(null);
                    cloudUser.saveAsync();
                    continue;
                }
                if (!member.getRoles().contains(role))
                    subserverGuild.addRoleToMember(cloudUser.getDiscordId(), role).queue();
            }
            subserverGuild.createCategory(categoryName).queue(category -> {
                category.createPermissionOverride(role).setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();
                category.createPermissionOverride(subserverGuild.getPublicRole()).setDeny(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();
                category.createTextChannel("chat").syncPermissionOverrides().queue();
                category.createVoiceChannel("Voice 1").syncPermissionOverrides().queue();
                category.createVoiceChannel("Voice 2").syncPermissionOverrides().queue();
            });
        });
        return true;
    }

    public void updateChannels(IGuild guild) {
        Guild subserverGuild = BukkitConfiguration.getMainGuild();
        Role role = subserverGuild.getRoleById(guild.getDiscordRole());
        if (role == null)
            return;
        if (!role.getName().equals(guild.getName())) {
            role.getManager().setName(guild.getName()).queue();
            String categoryName = "- " + guild.getName() + " -";
            List<Category> categoryList = subserverGuild.getCategoriesByName(categoryName, true);
            if (categoryList.size() == 0)
                return;
            categoryList.get(0).getManager().setName(guild.getName()).queue();
        }
        for (Member member : subserverGuild.getMembersWithRoles(role)) {
            cloudUserManagingService.loadUserByDiscordId(member.getId()).thenAccept(cloudUser -> {
                if (cloudUser == null || cloudUser.getGuild() != guild)
                    subserverGuild.removeRoleFromMember(member, role).queue();
            });
        }
        for (ICloudUser cloudUser : guild.getMember()) {
            if (cloudUser.getDiscordId() == null)
                continue;
            Member member = subserverGuild.getMemberById(cloudUser.getDiscordId());
            if (member != null) {
                addRole(cloudUser, member, role, subserverGuild);
                continue;
            }
            subserverGuild.retrieveMemberById(cloudUser.getDiscordId()).queue(member1 -> addRole(cloudUser, member1, role, subserverGuild));
        }
    }

    public void disposeGuildChannels(IGuild guild) {
        Guild subserverGuild = BukkitConfiguration.getMainGuild();
        String categoryName = "- " + guild.getName() + " -";
        List<Category> categoryList = subserverGuild.getCategoriesByName(categoryName, true);
        if (categoryList.size() == 0)
            return;
        Category category = categoryList.get(0);
        for (GuildChannel channel : category.getChannels())
            channel.delete().queue();
        category.delete().queue();
        Role role = subserverGuild.getRoleById(guild.getDiscordRole());
        if (role == null)
            return;
        role.delete().queue();
    }

    private void addRole(ICloudUser cloudUser, Member member, Role role, Guild subserverGuild) {
        if (member == null) {
            cloudUser.setDiscordId(null);
            cloudUser.saveAsync();
            return;
        }
        if (!member.getRoles().contains(role))
            subserverGuild.addRoleToMember(cloudUser.getDiscordId(), role).queue();
    }


}
