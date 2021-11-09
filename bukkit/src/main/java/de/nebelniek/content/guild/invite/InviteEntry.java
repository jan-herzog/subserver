package de.nebelniek.content.guild.invite;

import de.nebelniek.database.guild.interfaces.IGuild;
import de.nebelniek.database.user.interfaces.ICloudUser;

public record InviteEntry(IGuild guild, ICloudUser inviter) {
}
