package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.DateUtil;

import java.util.List;

public class FRIENDS_UPDATE extends MessageComposer {
    private final List<MessengerUser> friends;

    public FRIENDS_UPDATE(List<MessengerUser> friends) {
        this.friends = friends;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.friends.size());

        for (MessengerUser friend : this.friends) {
            Player player = PlayerManager.getInstance().getPlayerById(friend.getUserId());

            if (player != null) {
                friend.setFigure(player.getDetails().getFigure());
                friend.setLastOnline(player.getDetails().getLastOnline());
                friend.setSex(player.getDetails().getSex());
                friend.setConsoleMotto(player.getDetails().getConsoleMotto());
            }

            response.writeInt(friend.getUserId());
            response.writeString(friend.getConsoleMotto());

            boolean isOnline = (player != null);
            response.writeBool(isOnline);

            if (isOnline) {
                if (player.getRoomUser().getRoom() != null) {
                    Room room = player.getRoomUser().getRoom();

                    if (room.getData().getOwnerId() > 0) {
                        response.writeString("Floor1a");
                    } else {
                        response.writeString(room.getData().getPublicName());
                    }
                } else {
                    response.writeString("On hotel view");
                }
            } else {
                response.writeString(DateUtil.getDateAsString(friend.getLastOnline()));
            }

        }
    }

    @Override
    public short getHeader() {
        return 13; // "@M"
    }
}
