package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.HashMap;

public class USER_OBJ extends MessageComposer {
    private final PlayerDetails details;

    public USER_OBJ(PlayerDetails details) {
        this.details = details;
    }

    @Override
    public void compose(NettyResponse response) {
        final HashMap<String, String> userObj = new HashMap<>();

        userObj.put("user_id", String.valueOf(this.details.getId()));
        userObj.put("name", this.details.getName());
        userObj.put("figure", this.details.getFigure());
        userObj.put("sex", String.valueOf(this.details.getSex()));
        userObj.put("customData", "");
        userObj.put("ph_tickets", String.valueOf(this.details.getTickets()));
        userObj.put("ph_figure", this.details.getPoolFigure());
        userObj.put("photo_film", String.valueOf(this.details.getFilm())); //
        userObj.put("directMail", String.valueOf(this.details.isReceiveNews()));
        userObj.put("onlineStatus", "1");
        userObj.put("publicProfileEnabled", "1");
        userObj.put("friendRequestsEnabled", "1");
        userObj.put("offlineMessagingEnabled", "1");
        userObj.put("followMeEnabled", "1");
        userObj.put("requiresTutorial", "0");
        userObj.put("requiresRoomTutorial", "0");

        response.write(String.join("\r", userObj.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toArray(String[]::new)));
    }

    @Override
    public short getHeader() {
        return 5; // "@E"
    }
}
