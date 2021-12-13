package de.nebelniek.components.discord;

import com.github.expdev07.commy.core.Connection;
import com.github.expdev07.commy.core.handler.AbstractMessageHandler;
import de.nebelniek.message.DiscordIdUpdatePacket;
import org.bukkit.entity.Player;

public class DiscordIdUpdateHandler implements AbstractMessageHandler<Player, DiscordIdUpdatePacket> {

    @Override
    public void handle(Connection<Player> conn, String tag, DiscordIdUpdatePacket packet) {
        System.out.println("Received Packet with Uuid: " + packet.uuid() + " from " + conn.getSender() + " through " + tag);
    }

    @Override
    public Class<DiscordIdUpdatePacket> getMessageType() {
        return DiscordIdUpdatePacket.class;
    }


}
