package net.h4bbo.kepler.game.games.battleball.powerups;

import net.h4bbo.kepler.game.games.battleball.BattleBallGame;
import net.h4bbo.kepler.game.games.battleball.BattleBallTile;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallColourState;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallTileState;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.player.GameTeam;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.room.Room;

public class LightbulbHandle {
    public static void handle(BattleBallGame game, GamePlayer gamePlayer, Room room) {
        GameTeam gameTeam = gamePlayer.getTeam();

        for (Position position : gamePlayer.getPlayer().getRoomUser().getPosition().getCircle(5)) {
            BattleBallTile tile = (BattleBallTile) game.getTile(position.getX(), position.getY());

            if (tile == null ||
                    tile.getColour() == BattleBallColourState.DISABLED ||
                    tile.getState() == BattleBallTileState.SEALED) {
                continue;
            }

            BattleBallTileState state = tile.getState();
            BattleBallColourState colour = tile.getColour();

            if (state == BattleBallTileState.DEFAULT) {
                state = BattleBallTileState.TOUCHED; // Don't make it 4 hits, make it 3
            }


            BattleBallTileState newState = null;

            if (colour.getColourId() != gameTeam.getId()) {
                newState = BattleBallTileState.CLICKED;
            } else {
                newState = BattleBallTileState.getStateById(state.getTileStateId() + 1);
            }
            
            BattleBallColourState newColour = BattleBallColourState.getColourById(gameTeam.getId());

            tile.getNewPoints(gamePlayer, newState, newColour);
            tile.setColour(newColour);
            tile.setState(newState);

            if (newState == BattleBallTileState.SEALED) {
                tile.checkFill(gamePlayer, game.getFillTilesQueue());
            }

            //tile.addSealedPoints(gameTeam);
            game.getUpdateTilesQueue().add(tile);
        }

    }
}
