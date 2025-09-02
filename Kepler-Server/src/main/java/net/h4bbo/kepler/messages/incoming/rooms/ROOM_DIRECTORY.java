package net.h4bbo.kepler.messages.incoming.rooms;

import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.outgoing.navigator.CANTCONNECT;
import net.h4bbo.kepler.messages.outgoing.rooms.OPEN_CONNECTION;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class ROOM_DIRECTORY implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        char is_public = reader.contents().charAt(0);

        if (is_public != 'A') {
            player.send(new OPEN_CONNECTION());
            return;
        }

        reader.readBytes(1); // strip 'A'
        int roomId = reader.readInt();

        Room room = null;

        GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();

        if (roomId == -1 && gamePlayer != null && gamePlayer.isEnteringGame()) {
            room = gamePlayer.getGame().getRoom();
            room.getEntityManager().enterRoom(player, gamePlayer.getSpawnPosition());
        } else {
            room = RoomManager.getInstance().getRoomById(roomId);

            if (room == null) {
                return;
            }

            if (room.isClubOnly() && !player.getDetails().hasClubSubscription()) {
                player.send(new CANTCONNECT(CANTCONNECT.QueueError.CLUB_ONLY));
                return;
            }

            if (room.getData().getTotalVisitorsNow() >= room.getData().getTotalVisitorsMax() && !player.hasFuse(Fuseright.ENTER_FULL_ROOMS)) {
                player.send(new CANTCONNECT(CANTCONNECT.QueueError.FULL));
                return;
            }

            room.getEntityManager().enterRoom(player, null);
        }
     }
}
