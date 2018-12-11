package org.alexdev.kepler.messages.outgoing.games;

import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.TurnContainer;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class SNOWSTORM_FULLGAMESTATUS extends MessageComposer {
    private final SnowStormGame game;
    private final List<GameObject> objects;
    private final List<GameObject> events;
    private final GamePlayer gamePlayer;

    public SNOWSTORM_FULLGAMESTATUS(SnowStormGame game, GamePlayer gamePlayer, List<GameObject> objects, List<GameObject> events) {
        this.game = game;
        this.objects = objects;
        this.events = events;
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.game.getGameState().getStateId());
        response.writeInt(this.game.getPreparingGameSecondsLeft().get());
        response.writeInt(GameManager.getInstance().getPreparingSeconds(game.getGameType()));
        response.writeInt(this.objects.size());

        for (var obj : objects) {
            obj.serialiseObject(response);
        }

        response.writeBool(false);
        response.writeInt(this.game.getTeamAmount());
        response.writeInt(this.gamePlayer.getTurnContainer().getCurrentTurn());
        response.writeInt(this.gamePlayer.getTurnContainer().getCheckSum());
        response.writeInt(1);

        response.writeInt(this.events.size());

        for (GameObject gameObject : this.events) {
            gameObject.serialiseObject(response);
        }
    }

    @Override
    public short getHeader() {
        return 243; // "Cs"
    }
}
