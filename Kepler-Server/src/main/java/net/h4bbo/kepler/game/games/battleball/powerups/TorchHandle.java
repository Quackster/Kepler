package net.h4bbo.kepler.game.games.battleball.powerups;

import net.h4bbo.kepler.game.games.battleball.BattleBallGame;
import net.h4bbo.kepler.game.games.battleball.BattleBallTile;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallColourState;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallTileState;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.player.GameTeam;
import net.h4bbo.kepler.game.games.utils.TileUtil;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.room.Room;

import java.util.ArrayList;
import java.util.List;

public class TorchHandle {
    public static void handle(BattleBallGame game, GamePlayer gamePlayer, Room room) {
        GameTeam gameTeam = game.getTeamFor(gamePlayer);
        List<BattleBallTile> tilesToUpdate = new ArrayList<>();

        Position nextPosition = gamePlayer.getPlayer().getRoomUser().getPosition();

        while (TileUtil.isValidGameTile(gamePlayer, (BattleBallTile) game.getTile(nextPosition.getX(), nextPosition.getY()), false)) {
            nextPosition = nextPosition.getSquareInFront();

            if (!TileUtil.isValidGameTile(gamePlayer, (BattleBallTile) game.getTile(nextPosition.getX(), nextPosition.getY()), false)) {
                break;
            }

            tilesToUpdate.add((BattleBallTile) game.getTile(nextPosition.getX(), nextPosition.getY()));

        }

        for (BattleBallTile tile : tilesToUpdate) {
            if (tile.getState() == BattleBallTileState.SEALED) {
                continue;
            }

            if (tile.getColour() == BattleBallColourState.DISABLED) {
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

            game.getUpdateTilesQueue().add(tile);
        }
    }
}
