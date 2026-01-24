package net.h4bbo.kepler.messages.outgoing.games;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameEvent;
import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.battleball.BattleBallTile;
import net.h4bbo.kepler.game.games.enums.GameType;
import net.h4bbo.kepler.game.games.player.GameTeam;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.Collection;
import java.util.List;

/**
 * Consolidated GAMESTATUS composer
 * - SnowStorm (default / standard)
 * - BattleBall
 */
public class GAMESTATUS extends MessageComposer {

    private enum Mode {
        SNOWSTORM,
        BATTLEBALL
    }

    private final Mode mode;

    // ===== BattleBall fields =====
    private final Game game;
    private final Collection<GameTeam> gameTeams;
    private final List<GameObject> objects;
    private final List<GameEvent> events;
    private final List<BattleBallTile> updateTiles;
    private final List<BattleBallTile> fillTiles;

    // ===== SnowStorm fields =====
    private final List<List<GameObject>> turns;
    private final int currentTurn;
    private final int currentCheckSum;

    /* ---------------- SnowStorm constructor ---------------- */
    public GAMESTATUS(List<List<GameObject>> turns, int currentTurn, int currentCheckSum) {
        this.mode = Mode.SNOWSTORM;

        this.turns = turns;
        this.currentTurn = currentTurn;
        this.currentCheckSum = currentCheckSum;

        // BattleBall unused
        this.game = null;
        this.gameTeams = null;
        this.objects = null;
        this.events = null;
        this.updateTiles = null;
        this.fillTiles = null;
    }

    /* ---------------- BattleBall constructor ---------------- */
    public GAMESTATUS(
            Game game,
            Collection<GameTeam> gameTeams,
            List<GameObject> objects,
            List<GameEvent> events,
            List<BattleBallTile> updateTiles,
            List<BattleBallTile> fillTiles
    ) {
        this.mode = Mode.BATTLEBALL;

        this.game = game;
        this.gameTeams = gameTeams;
        this.objects = objects;
        this.events = events;
        this.updateTiles = updateTiles;
        this.fillTiles = fillTiles;

        // SnowStorm unused
        this.turns = null;
        this.currentTurn = 0;
        this.currentCheckSum = 0;
    }

    @Override
    public void compose(NettyResponse response) {
        if (this.mode == Mode.SNOWSTORM) {
            composeSnowStorm(response);
        } else {
            composeBattleBall(response);
        }
    }

    /* ---------------- SnowStorm ---------------- */
    private void composeSnowStorm(NettyResponse response) {
        response.writeInt(this.currentTurn);
        response.writeInt(this.currentCheckSum);
        response.writeInt(this.turns == null || this.turns.isEmpty() ? 1 : this.turns.size());

        if (this.turns == null) {
            return;
        }

        for (var turn : this.turns) {
            response.writeInt(turn.size());

            for (GameObject gameObject : turn) {
                gameObject.serialiseObject(response);
            }
        }
    }

    /* ---------------- BattleBall ---------------- */
    private void composeBattleBall(NettyResponse response) {
        response.writeInt(this.objects.size());

        for (GameObject gameObject : this.objects) {
            response.writeInt(gameObject.getGameObjectType().getObjectId());
            gameObject.serialiseObject(response);
        }

        if (this.game.getGameType() == GameType.BATTLEBALL) {
            response.writeInt(this.updateTiles.size());

            for (BattleBallTile tile : this.updateTiles) {
                response.writeInt(tile.getPosition().getX());
                response.writeInt(tile.getPosition().getY());
                response.writeInt(tile.getColour().getColourId());
                response.writeInt(tile.getState().getTileStateId());
            }

            response.writeInt(this.fillTiles.size());

            for (BattleBallTile tile : this.fillTiles) {
                response.writeInt(tile.getPosition().getX());
                response.writeInt(tile.getPosition().getY());
                response.writeInt(tile.getColour().getColourId());
                response.writeInt(tile.getState().getTileStateId());
            }
        }

        response.writeInt(this.gameTeams.size());
        for (GameTeam team : this.gameTeams) {
            response.writeInt(team.getPoints());
        }

        response.writeInt(1); // static value preserved

        response.writeInt(this.events.size());
        for (GameEvent gameEvent : this.events) {
            response.writeInt(gameEvent.getGameEventType().getEventId());
            gameEvent.serialiseEvent(response);
        }
    }

    @Override
    public short getHeader() {
        return 244;
    }
}
