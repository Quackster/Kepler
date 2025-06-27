package org.alexdev.kepler.messages.incoming.rooms.groups;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.groups.GROUP_BADGES;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.HashMap;

public class GET_GROUP_BADGES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();
        HashMap<Integer, String> groupBadges = new HashMap<>();

        for (Player p : room.getEntityManager().getPlayers()) {

            if (p.getDetails().getFavouriteGroupId() > 0) {
                if (groupBadges.containsKey(p.getDetails().getFavouriteGroupId())) {
                    continue;
                }

                var group = player.getJoinedGroup(p.getDetails().getFavouriteGroupId());

                if (group == null) {
                    continue;
                }

                groupBadges.put(group.getId(), group.getBadge());
            }
        }

        player.send(new GROUP_BADGES(groupBadges));
    }
}