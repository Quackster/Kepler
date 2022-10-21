package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.config.GameConfiguration;


import org.alexdev.kepler.game.ads.AdManager;
import org.alexdev.kepler.game.ads.Advertisement;
import org.alexdev.kepler.messages.outgoing.rooms.INTERSITIALDATA;

import java.util.concurrent.ThreadLocalRandom;

public class GETINTEREST implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!GameConfiguration.getInstance().getBoolean("room.intersitial.ads")) {
            player.send(new INTERSITIALDATA(null, null));
            return;
        }

        if (ThreadLocalRandom.current().nextInt(5 + 1) != 5) {
            player.send(new INTERSITIALDATA(null, null));
            return;
        }

        String image = null;
        String url = null;

        Advertisement advertisement = AdManager.getInstance().getRandomLoadingAd();

        if (advertisement != null) {

            if (advertisement.getImage() != null) {
                image = advertisement.getImage();
            }

            if (advertisement.getUrl() != null) {
                url = advertisement.getUrl().replace("https", "http");
            }
        }

        player.send(new INTERSITIALDATA(image, url));
    }
}
