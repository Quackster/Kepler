package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.game.messenger.Messenger;
import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.messages.types.PlayerMessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class FRIENDS_UPDATE extends PlayerMessageComposer {
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
        if (getPlayer().getVersion() < 23) {
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
                            response.writeString(room.getData().getName());
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
        } else {
            response.writeInt(0); // categories
            response.writeInt(this.friendsUpdated. size());

            for (MessengerUser friend : this.friendsUpdated) {
                response.writeInt(0);
                friend.serialise(this.player, response);
            }
        }
    }

    @Override
    public short getHeader() {
        return 13; // "@M"
    }
}
