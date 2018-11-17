package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.messenger.FOLLOW_ERROR;
import org.alexdev.kepler.messages.outgoing.messenger.ROOMFORWARD;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class FOLLOW_FRIEND implements MessageEvent {
    private enum FollowErrors {
        NOT_FRIEND(0),
        OFFLINE(1),
        ON_HOTELVIEW(2),
        NO_CREEPING_ALLOWED(3);

        private int id;

        FollowErrors(int id) {
            this.id = id;
        }

        public int getErrorId(){
            return id;
        }
    }

    @Override
    public void handle(Player player, NettyRequest reader) {
        int friendId = reader.readInt();

        if (!player.getMessenger().hasFriend(friendId)) {
            player.send(new FOLLOW_ERROR(FollowErrors.NOT_FRIEND.getErrorId())); // Not their friend
            return;
        }

        Player friend = PlayerManager.getInstance().getPlayerById(friendId);

        if (friend == null) {
            player.send(new FOLLOW_ERROR(FollowErrors.OFFLINE.getErrorId())); // Friend is not online
            return;
        }

        if (friend.getRoomUser().getRoom() == null) {
            player.send(new FOLLOW_ERROR(FollowErrors.ON_HOTELVIEW.getErrorId())); // Friend is on hotelview
            return;
        }

        if (!friend.getDetails().doesAllowStalking()) {
            player.send(new FOLLOW_ERROR(FollowErrors.NO_CREEPING_ALLOWED.getErrorId())); // Friend does not allow stalking
            return;
        }

        Room friendRoom = friend.getRoomUser().getRoom();

        boolean isPublic = friendRoom.isPublicRoom();
        int roomId = friendRoom.getId();

        // If you tried to follow someone in arena, send them to lobby.
        if (friendRoom.getData().isGameArena()) {
            String modelType = friendRoom.getData().getGameLobby();
            roomId = RoomManager.getInstance().getRoomByModel(modelType).getId();
            isPublic = true;
        }

        if (isPublic) { // Some weird offset shit required...
            Room room = RoomManager.getInstance().getRoomById(roomId);

            if (room.getData().isNavigatorHide()) {
                roomId = room.getFollowRedirect();
            }

           // roomId = roomId + RoomManager.PUBLIC_ROOM_OFFSET;
        }

        player.send(new ROOMFORWARD(isPublic, roomId));
    }
}
