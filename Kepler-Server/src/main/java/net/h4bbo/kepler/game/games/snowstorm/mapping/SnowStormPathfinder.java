package org.alexdev.kepler.game.games.snowstorm.mapping;

import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormPlayers;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormMath;
import org.alexdev.kepler.game.pathfinder.Pathfinder;
import org.alexdev.kepler.game.pathfinder.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SnowStormPathfinder {
    private static final int MAX_PATHFIND_ITERATIONS = 50;

    /**
     * Check if the player can walk to the target position by simulating the pathfinding.
     */
    public static boolean canWalkTo(SnowStormGame game, GamePlayer player, Position current, Position target) {
        if (current == null || target == null) {
            return false;
        }

        int localX = current.getX();
        int localY = current.getY();
        int targetX = target.getX();
        int targetY = target.getY();

        for (int i = 0; i < MAX_PATHFIND_ITERATIONS; i++) {
            // Already at target
            if (localX == targetX && localY == targetY) {
                return true;
            }

            // Calculate direction to target (using world coordinates for angle calculation)
            int worldCurrentX = SnowStormMath.tileToWorld(localX);
            int worldCurrentY = SnowStormMath.tileToWorld(localY);
            int worldTargetX = SnowStormMath.tileToWorld(targetX);
            int worldTargetY = SnowStormMath.tileToWorld(targetY);

            int rot = SnowStormMath.getAngleFromComponents(worldTargetX - worldCurrentX, worldTargetY - worldCurrentY);
            int direction = SnowStormMath.direction360To8(rot);

            // Try direct direction, then CCW, then CW
            Integer[] nextTile = getTileNeighborInDirection(game, player, localX, localY, direction);

            if (nextTile == null) {
                int ccwDir = (direction + 7) % 8;
                nextTile = getTileNeighborInDirection(game, player, localX, localY, ccwDir);

                if (nextTile == null) {
                    int cwDir = (direction + 1) % 8;
                    nextTile = getTileNeighborInDirection(game, player, localX, localY, cwDir);

                    if (nextTile == null) {
                        return false;
                    }
                }
            }

            localX = nextTile[0];
            localY = nextTile[1];
        }

        return true;
    }

    public static Position getNextDirection(SnowStormGame snowStormGame, GamePlayer gamePlayer) {
        List<Position> positions = new ArrayList<>();
        var attributes = SnowStormPlayers.get(gamePlayer);

        for (Position POINT : Pathfinder.DIAGONAL_MOVE_POINTS) {
            var temp = attributes.getCurrentPosition().copy().add(POINT);

            if (!isValidTile(snowStormGame, gamePlayer, temp)) {
                continue;
            }

            positions.add(temp);
        }

        positions.sort(Comparator.comparingDouble(pos -> pos.getDistanceSquared(attributes.getWalkGoal())));
        return (positions.size() > 0 ? positions.get(0) : null);
    }

    public static Integer[] getTileNeighborInDirection(SnowStormGame snowStormGame, GamePlayer gamePlayer, int tX,int tY,int tdir) {
        Position a  = new Position(0,0);
        switch(tdir)
        {
            case 0:
                a.setX(tX);
                a.setY(tY-1);
                if(isValidTile(snowStormGame,gamePlayer,a)){
                    return new Integer[]{tX,tY - 1};
                }
            break;
            case 1:

                a.setX(tX + 1);
                a.setY(tY - 1);
                if(isValidTile(snowStormGame,gamePlayer,a)){
                    return new Integer[]{tX + 1,tY - 1};
                }
                break;
            case 2:
                a.setX(tX + 1);
                a.setY(tY);
                if(isValidTile(snowStormGame,gamePlayer,a)){
                    return new Integer[]{tX + 1,tY};
                }
                break;
            case 3:
                a.setX(tX + 1);
                a.setY(tY + 1);
                if(isValidTile(snowStormGame,gamePlayer,a)){
                    return new Integer[]{tX + 1,tY + 1};
                }
                break;
            case 4:
                a.setX(tX);
                a.setY(tY+1);
                if(isValidTile(snowStormGame,gamePlayer,a)){
                    return new Integer[]{tX,tY + 1};
                }
                break;
            case 5:
                a.setX(tX - 1);
                a.setY(tY + 1);
                if(isValidTile(snowStormGame,gamePlayer,a)){
                    return new Integer[]{tX - 1,tY + 1};
                }
                break;
            case 6:
                a.setX(tX - 1);
                a.setY(tY);
                if(isValidTile(snowStormGame,gamePlayer,a)){
                    return new Integer[]{tX - 1,tY};
                }
                break;
            case 7:
                a.setX(tX - 1);
                a.setY(tY - 1);
                if(isValidTile(snowStormGame,gamePlayer,a)){
                    return new Integer[]{tX - 1,tY - 1};
                }
                break;
        }
        return null;
    }


    public static boolean isValidTile(SnowStormGame snowStormGame, GamePlayer gamePlayer, Position tmp) {
        var tile = snowStormGame.getMap().getTile(tmp);

        if (tile == null || !tile.isWalkable()) {
            return false;
        }

        for (GamePlayer player : snowStormGame.getActivePlayers()) {
            if (player.getPlayer().getDetails().getId() == gamePlayer.getPlayer().getDetails().getId()) {
                continue;
            }

            var playerAttributes = SnowStormPlayers.get(player);

            if (playerAttributes.getNextGoal() != null) {
                if (playerAttributes.getNextGoal().equals(tmp)) {
                    return false;
                }
            } else {
                if (playerAttributes.getCurrentPosition().equals(tmp)) {
                    return false;
                }
            }
        }

        return true;
    }
}
