package net.h4bbo.kepler.messages.outgoing.messenger;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;
import net.h4bbo.kepler.util.DateUtil;

import java.util.List;

public class MESSENGER_SEARCH extends MessageComposer {
    private PlayerDetails details;

    private List<PlayerDetails> friends;
    private List<PlayerDetails> others;


    public MESSENGER_SEARCH(PlayerDetails details) {
        this.details = details;
    }

    public MESSENGER_SEARCH(List<PlayerDetails> friends, List<PlayerDetails> others) {
        this.friends = friends;
        this.others = others;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString("MESSENGER");

        if (this.details != null) {
            new MessengerUser(this.details).serialise(response);
        } else {
            response.writeInt(0);
        }
    }

    private void serialiseSearch(NettyResponse response, PlayerDetails playerDetails) {
        response.writeInt(playerDetails.getId());
        response.writeString(playerDetails.getName());
        response.writeString(playerDetails.getMotto());

        Player player = PlayerManager.getInstance().getPlayerById(playerDetails.getId());
        boolean isOnline = player != null;

        response.writeBool(isOnline);
        response.writeBool(isOnline && player.getRoomUser().getRoom() != null);
        response.writeString((isOnline && player.getRoomUser().getRoom() != null) ? player.getRoomUser().getRoom().getData().getName() : "");

        response.writeBool(playerDetails.getSex().equalsIgnoreCase("M"));
        response.writeString(isOnline ? playerDetails.getFigure() : "");
        response.writeString(DateUtil.getDateAsString(playerDetails.getLastOnline()));
    }

    @Override
    public short getHeader() {
        return 128;
    }
}
