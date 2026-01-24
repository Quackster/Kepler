package net.h4bbo.kepler.messages.outgoing.messenger;

import net.h4bbo.kepler.game.messenger.Messenger;
import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;
import net.h4bbo.kepler.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class FRIENDS_UPDATE extends MessageComposer {
    private final List<MessengerUser> friends;
    private final Messenger messenger;
    private final Player player;

    private List<MessengerUser> friendsAdded;
    private List<MessengerUser> friendsRemoved;
    private List<MessengerUser> friendsUpdated;

    public FRIENDS_UPDATE(Player player, Messenger messenger) {
        this.messenger = messenger;
        this.player = player;

        this.friends = new ArrayList<>();

        this.friendsAdded = new ArrayList<>();
        this.friendsRemoved = new ArrayList<>();
        this.friendsUpdated = new ArrayList<>();

        this.messenger.getFriendsUpdate().drainTo(this.friendsUpdated);

        this.friends.addAll(this.friendsAdded);
        this.friends.addAll(this.friendsRemoved);
        this.friends.addAll(this.friendsUpdated);
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.friends.size());

        for (MessengerUser friend : this.friends) {
            response.writeInt(friend.getUserId());
            response.writeString(friend.getConsoleMotto());

            Player player = PlayerManager.getInstance().getPlayerById(friend.getUserId());

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
