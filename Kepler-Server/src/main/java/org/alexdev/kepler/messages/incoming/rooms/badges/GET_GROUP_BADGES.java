package org.alexdev.kepler.messages.incoming.rooms.badges;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.badges.AVAILABLE_BADGES;
import org.alexdev.kepler.messages.outgoing.rooms.badges.GROUP_BADGES;
import org.alexdev.kepler.messages.outgoing.rooms.badges.GROUP_MEMBERSHIP_UPDATE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_GROUP_BADGES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        for (Entity entity : player.getRoomUser().getRoom().getEntities()) {
            if(entity.getType() == EntityType.PLAYER) {
                System.out.println("Player: " + entity.getDetails().getName());
            }
        }
        player.send(new GROUP_BADGES());
        player.send(new GROUP_MEMBERSHIP_UPDATE());
    }
}