package net.h4bbo.kepler.game.games.snowstorm.mapping;

import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowballObject;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormActivityState;
import net.h4bbo.kepler.game.pathfinder.Pathfinder;
import net.h4bbo.kepler.game.pathfinder.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SnowStormPathfinder {
    public static Position getNextDirection(SnowStormGame snowStormGame, GamePlayer gamePlayer) {
        List<Position> positions = new ArrayList<>();

        for (Position POINT : Pathfinder.DIAGONAL_MOVE_POINTS) {
            var temp = gamePlayer.getSnowStormAttributes().getCurrentPosition().copy().add(POINT);

            if (!isValidTile(snowStormGame, gamePlayer, temp)) {
                continue;
            }

            positions.add(temp);
        }

        positions.sort(Comparator.comparingDouble(pos -> pos.getDistanceSquared(gamePlayer.getSnowStormAttributes().getWalkGoal())));
        return (positions.size() > 0 ? positions.get(0) : null);
    }

    private static boolean isValidTile(SnowStormGame snowStormGame, GamePlayer gamePlayer, Position tmp) {
        for (GamePlayer player : snowStormGame.getActivePlayers()) {
            if (player.getPlayer().getDetails().getId() == gamePlayer.getPlayer().getDetails().getId()) {
                continue;
            }

            if (gamePlayer.getSnowStormAttributes().getCurrentPosition().equals(tmp)) {
                return false;
            }

            if (gamePlayer.getSnowStormAttributes().getNextGoal() != null && gamePlayer.getSnowStormAttributes().getNextGoal().equals(tmp)) {
                return false;
            }
        }

        var tile = snowStormGame.getMap().getTile(tmp);

        if (tile == null) {
            return false;
        }

        return tile.isWalkable();
    }

    /**
     * Check if the point (x0, y0) can see point (x1, y1) by drawing a line
     * and testing for the "blocking" property at each new tile. Returns the
     * points on the line if it is, in fact, visible. Otherwise, returns an
     * empty list (rather than null - Efficient Java, item #43).
     *
     * http://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
     */
    public static LinkedList<Position> getMaxVisibility(SnowballObject snowballObject, int x0, int y0, int x1, int y1, SnowballObject.SnowballTrajectory trajectory) {
        LinkedList<Position> line = new LinkedList<>();
        line.add(new Position(x0, y0));
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = (x0 < x1) ? 1 : -1;
        int sy = (y0 < y1) ? 1 : -1;
        int err = dx - dy;
        int e2;

        while (!(x0 == x1 && y0 == y1)) {
            if (isBlockedTile(snowballObject, new Position(x0, y0))) {
                //line.clear();
                line.add(new Position(x0, y0));
                return line;
            }

            e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }

            line.add(new Position(x0, y0));
        }

        return line;
    }

    public static GamePlayer getOppositionPlayer(SnowStormGame snowStormGame, GamePlayer thrower, Position position) {
        for (GamePlayer player : snowStormGame.getActivePlayers()) {
            if ((player.getSnowStormAttributes().getCurrentPosition().equals(position) || (player.getSnowStormAttributes().getNextGoal() != null && player.getSnowStormAttributes().getNextGoal().equals(player)))) {
                if (player.getSnowStormAttributes().getActivityState() == SnowStormActivityState.ACTIVITY_STATE_STUNNED) {
                    continue;
                }

                if (snowStormGame.isOppositionPlayer(thrower, player)) {
                    return player;
                }
            }
        }

        return null;
    }

    public static boolean isBlockedTile(SnowballObject snowballObject, Position position) {
        if (snowballObject.getTrajectory() != SnowballObject.SnowballTrajectory.LONG_TRAJECTORY) {
            for (GamePlayer player : snowballObject.getGame().getActivePlayers()) {
                if ((player.getSnowStormAttributes().getCurrentPosition().equals(position) || (player.getSnowStormAttributes().getNextGoal() != null && player.getSnowStormAttributes().getNextGoal().equals(player)))) {
                    if (player.getSnowStormAttributes().getActivityState() == SnowStormActivityState.ACTIVITY_STATE_STUNNED) {
                        continue;
                    }

                    if (snowballObject.getGame().isOppositionPlayer(snowballObject.getThrower(), player)) {
                        return true;
                    }
                }
            }
        }

        var tile = snowballObject.getGame().getMap().getTile(position);

        if (tile == null) {
            return false;
        }

        return tile.isHeightBlocking(snowballObject.getTrajectory());
    }
}

        /*
          tMoveTarget = me.pGameObjectFinalTarget
  tNextTarget = me.pGameObjectNextTarget
  if not objectp(tMoveTarget) then
    return FALSE
  end if
  tMoveTargetX = tMoveTarget.x
  tMoveTargetY = tMoveTarget.y
  if not objectp(me.pGameObjectLocation) then
    return FALSE
  end if
  tCurrentX = me.pGameObjectLocation.x
  tCurrentY = me.pGameObjectLocation.y
  if not objectp(tNextTarget) then
    return FALSE
  end if
  tNextTargetX = tNextTarget.x
  tNextTargetY = tNextTarget.y
  if (tCurrentX = tMoveTargetX) and (tCurrentY = tMoveTargetY) then
    return FALSE
  end if
  if tNextTargetX <> tCurrentX or tNextTargetY <> tCurrentY then
    tOldX = tCurrentX
    tOldY = tCurrentY
    tTargetX = tNextTarget.x
    tDeltaX = (tTargetX - tCurrentX)
    if tDeltaX < 0 then
      if tDeltaX > -SUBTURN_MOVEMENT then
        tCurrentX = tTargetX
      else
        tCurrentX = (tCurrentX - SUBTURN_MOVEMENT)
      end if
    else
      if tDeltaX > 0 then
        if tDeltaX < SUBTURN_MOVEMENT then
          tCurrentX = tTargetX
        else
          tCurrentX = (tCurrentX + SUBTURN_MOVEMENT)
        end if
      end if
    end if
    tTargetY = tNextTarget.y
    tDeltaY = (tTargetY - tCurrentY)
    if tDeltaY < 0 then
      if tDeltaY > -SUBTURN_MOVEMENT then
        tCurrentY = tTargetY
      else
        tCurrentY = (tCurrentY - SUBTURN_MOVEMENT)
      end if
    else
      if tDeltaY > 0 then
        if tDeltaY < SUBTURN_MOVEMENT then
          tCurrentY = tTargetY
        else
          tCurrentY = (tCurrentY + SUBTURN_MOVEMENT)
        end if
      end if
    end if
         */
