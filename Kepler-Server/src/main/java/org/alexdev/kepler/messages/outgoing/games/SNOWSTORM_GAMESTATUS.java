package org.alexdev.kepler.messages.outgoing.games;

import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class SNOWSTORM_GAMESTATUS extends MessageComposer {
    private final List<GameObject> events;
    private final GamePlayer gamePlayer;

    public SNOWSTORM_GAMESTATUS(GamePlayer gamePlayer, List<GameObject> events) {
        this.events = events;
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.gamePlayer.getTurnContainer().getCurrentTurn());
        response.writeInt(this.gamePlayer.getTurnContainer().getCheckSum());
        response.writeInt(0);

       /* response.writeInt(this.events.size());

        for (GameObject gameObject : this.events) {
            gameObject.serialiseObject(response);
        }*/
    }

    @Override
    public short getHeader() {
        return 244; // "Cs"
    }
}
