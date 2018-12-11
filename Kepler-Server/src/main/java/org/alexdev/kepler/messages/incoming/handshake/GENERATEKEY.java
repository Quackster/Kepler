package org.alexdev.kepler.messages.incoming.handshake;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.handshake.AVAILABLE_SETS;
import org.alexdev.kepler.messages.outgoing.handshake.SESSION_PARAMETERS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GENERATEKEY implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.isLoggedIn()) {
            return;
        }

        player.send(new SESSION_PARAMETERS(player.getDetails()));
        player.send(new AVAILABLE_SETS("[100,105,110,115,120,125,130,135,140,145,150,155,160,165,170,175,176,177,178,180,185,190,195,200,205,206,207,210,215,220,225,230,235,240,245,250,255,260,265,266,267,270,275,280,281,285,290,295,300,305,500,505,510,515,520,525,530,535,540,545,550,555,565,570,575,580,585,590,595,596,600,605,610,615,620,625,626,627,630,635,640,645,650,655,660,665,667,669,670,675,680,685,690,695,696,700,705,710,715,720,725,730,735,740]"));
    }
}
