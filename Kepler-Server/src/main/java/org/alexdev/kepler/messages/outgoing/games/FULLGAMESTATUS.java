package org.alexdev.kepler.messages.outgoing.games;

import java.util.ArrayList;
import java.util.List;
import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.battleball.BattleBallTile;
import org.alexdev.kepler.game.games.enums.GameState;
import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.tasks.SnowStormGameTask;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class FULLGAMESTATUS extends MessageComposer {
    private final Game game;

    public FULLGAMESTATUS(Game game) {
        this.game = game;
    }

    @Override
    public void compose(NettyResponse response) {
        if (this.game.getGameType() == GameType.BATTLEBALL) {
            composeBattleBall(response);
            return;
        }

        composeSnowStorm(response);
    }

    private void composeBattleBall(NettyResponse response) {
        response.writeInt(GameState.STARTED.getStateId());
        response.writeInt(this.game.getPreparingGameSecondsLeft().get());
        response.writeInt(GameManager.getInstance().getPreparingSeconds(this.game.getGameType()));
        response.writeInt(this.game.getObjects().size());

        for (GameObject gameObject : this.game.getObjects()) {
            response.writeInt(gameObject.getGameObjectType().getObjectId());
            gameObject.serialiseObject(response);
        }

        response.writeInt(this.game.getRoomModel().getMapSizeY());
        response.writeInt(this.game.getRoomModel().getMapSizeX());

        for (int y = 0; y < this.game.getRoomModel().getMapSizeY(); y++) {
            for (int x = 0; x < this.game.getRoomModel().getMapSizeX(); x++) {
                BattleBallTile tile = (BattleBallTile) this.game.getTile(x, y);
                writeBattleBallTile(response, tile);
            }
        }

        response.writeInt(1);
        response.writeInt(0);
    }

    private void writeBattleBallTile(NettyResponse response, BattleBallTile tile) {
        if (tile == null) {
            response.writeInt(-1);
            response.writeInt(0);
            return;
        }

        response.writeInt(tile.getColour().getColourId());
        response.writeInt(tile.getState().getTileStateId());
    }

    private void composeSnowStorm(NettyResponse response) {
        List<GameObject> objects = this.game.getObjects();
        SnowStormGameTask snowStormTask = ((SnowStormGame) this.game).getUpdateTask();
        List<List<GameObject>> turns = new ArrayList<>();
        int currentTurn = 0;
        int currentCheckSum = 0;

        if (snowStormTask != null) {
            turns = snowStormTask.getExecutingTurns();
            currentTurn = snowStormTask.getCurrentTurn();
            currentCheckSum = snowStormTask.getCurrentChecksum();
        }

        response.writeInt(this.game.getGameState().getStateId());
        response.writeInt(this.game.getPreparingGameSecondsLeft().get());
        response.writeInt(GameManager.getInstance().getPreparingSeconds(this.game.getGameType()));
        response.writeInt(this.game.getObjects().size());

        for (GameObject object : objects) {
            object.serialiseObject(response);
        }

        response.writeBool(false);
        response.writeInt(this.game.getTeamAmount());

        new GAMESTATUS(turns, currentTurn, currentCheckSum).compose(response);
    }

    @Override
    public short getHeader() {
        return 243;
    }
}
