package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.game.ads.AdManager;
import org.alexdev.kepler.game.ads.Advertisement;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.ROOMAD;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.config.GameConfiguration;

public class GETROOMAD implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        String image = null;
        String url = null;

        if (room.isPublicRoom()) {
            Advertisement advertisement = AdManager.getInstance().getRandomAd(room.getId());

            if (advertisement != null) {

                if (advertisement.getImage() != null) {
                    image = advertisement.getImage();
                }

                if (advertisement.getUrl() != null) {
                    url = advertisement.getUrl();
                }
            }
        }

        if (!GameConfiguration.getInstance().getBoolean("room.ads")) {
            image = null;
            url = null;
        }

        player.send(new ROOMAD(image, url));
    }
}
