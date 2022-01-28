package org.alexdev.kepler.messages.incoming.rooms.badges;

import org.alexdev.kepler.dao.mysql.GroupDao;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.badges.GROUP_BADGES;
import org.alexdev.kepler.messages.outgoing.rooms.badges.GROUP_DETAILS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_GROUP_DETAILS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        int groupId = reader.readInt();
        System.out.println("groupid: " + groupId);

        player.send(new GROUP_DETAILS(GroupDao.getGroup(groupId)));
    }
}