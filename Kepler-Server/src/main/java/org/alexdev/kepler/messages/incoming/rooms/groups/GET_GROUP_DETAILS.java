package org.alexdev.kepler.messages.incoming.rooms.groups;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.groups.GROUP_INFO;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_GROUP_DETAILS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        int groupId = reader.readInt();

        Room room = player.getRoomUser().getRoom();

        for (Player p : room.getEntityManager().getPlayers()) {
            var group = p.getJoinedGroup(groupId);

            if (group == null) {
                continue;
            }

            player.send(new GROUP_INFO(group));
            break;
        }
    }
}