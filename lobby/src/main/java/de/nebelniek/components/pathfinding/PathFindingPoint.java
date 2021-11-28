package de.nebelniek.components.pathfinding;

import net.minecraft.core.particles.Particles;
import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public record PathFindingPoint(double x, double y, double z) {

    public void spawn() {
        World world = Bukkit.getWorld("world");
        Location location = new Location(world, x + .5, y + .3, z + .5);
        PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(Particles.A, true, x + .5, y + .3, z + .5, 0, 0, 0, 0, 1);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            ((CraftPlayer) onlinePlayer).getHandle().b.sendPacket(particles);
    }

}