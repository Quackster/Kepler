package org.alexdev.kepler.messages.incoming.rooms.badges;

import org.alexdev.kepler.dao.mysql.GroupDao;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.group.Group;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.badges.AVAILABLE_BADGES;
import org.alexdev.kepler.messages.outgoing.rooms.badges.GROUP_BADGES;
import org.alexdev.kepler.messages.outgoing.rooms.badges.GROUP_MEMBERSHIP_UPDATE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.List;

public class GET_GROUP_BADGES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        List<Integer> groupIds = new ArrayList<>();
        List<Group> groups = new ArrayList<>();
        for (Entity entity : player.getRoomUser().getRoom().getEntities()) {
            if(entity.getType() == EntityType.PLAYER) {
                System.out.println("Player: " + entity.getDetails().getName());
                if(entity.getDetails().getGroup() > 0) {
                    groupIds.add(entity.getDetails().getGroup());
                    player.send(new GROUP_MEMBERSHIP_UPDATE(entity.getDetails()));
                }
            }
        }

        for (int i : groupIds) {
            groups.add(GroupDao.getGroup(i));
        }
        player.send(new GROUP_BADGES(groups));

    }
}