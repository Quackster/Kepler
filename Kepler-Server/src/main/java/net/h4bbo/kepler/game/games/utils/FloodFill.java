package net.h4bbo.kepler.game.games.utils;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.battleball.BattleBallTile;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallColourState;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallTileState;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.pathfinder.Pathfinder;
import net.h4bbo.kepler.game.pathfinder.Position;

import java.util.*;

public class FloodFill {
    public static Collection<BattleBallTile> getFill(GamePlayer gamePlayer, BattleBallTile startTile) {
        HashSet<BattleBallTile> closed = new HashSet<>();

        ArrayDeque<BattleBallTile> stack = new ArrayDeque<>();
        stack.add(startTile);

        while (stack.size() > 0) {
            BattleBallTile tile = stack.pollLast();

            if (tile != null) {
                for (BattleBallTile loopTile : neighbours(gamePlayer.getGame(), tile.getPosition())) {
                    if (loopTile == null) {
                        closed.clear();
                        return closed;
                    }

                    if (loopTile.getColour() == BattleBallColourState.DISABLED) {
                        closed.clear();
                        return closed;
                    }

                    if ((loopTile.getColour().getColourId() != gamePlayer.getTeamId() || loopTile.getState() != BattleBallTileState.SEALED) && !closed.contains(loopTile) && !stack.contains(loopTile)) {
                        stack.addFirst(loopTile);
                    }
                }

                closed.add(tile);
            }
        }

        return closed;
    }

    public static HashSet<BattleBallTile> neighbours(Game game, Position position) {
        HashSet<BattleBallTile> battleballTiles = new HashSet<>();

        for (Position point : Pathfinder.MOVE_POINTS) {
            Position tmp = position.copy().add(point);
            battleballTiles.add((BattleBallTile) game.getTile(tmp.getX(), tmp.getY()));
        }

        return battleballTiles;
    }
}