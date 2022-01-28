package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.dao.mysql.GroupDao;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.group.Group;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.incoming.rooms.badges.GET_GROUP_BADGES;
import org.alexdev.kepler.messages.outgoing.rooms.badges.GROUP_BADGES;
import org.alexdev.kepler.messages.outgoing.rooms.badges.GROUP_MEMBERSHIP_UPDATE;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_OBJECTS;
import org.alexdev.kepler.messages.outgoing.user.USER_TAGS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.List;

public class G_USRS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        if (player.getRoomUser().getGamePlayer() != null && player.getRoomUser().getGamePlayer().isInGame()) {
            return; // Not needed for game arenas
        }

        Room room = player.getRoomUser().getRoom();
        player.send(new USER_OBJECTS(room.getEntities()));

        List<Integer> groupIds = new ArrayList<>();
        List<Group> groups = new ArrayList<>();

        for (Entity entity : room.getEntities()) {
            if(entity.getType() == EntityType.PLAYER) {
                player.send(new USER_TAGS(entity.getDetails()));
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
