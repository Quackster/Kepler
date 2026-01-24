package org.alexdev.kepler.game.games.battleball;

import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Tracks which players are currently coloring tiles on behalf of another player (Harlequin power-up).
 */
public final class BattleBallHarlequinManager {
    private final ConcurrentMap<Integer, Integer> colouringTargets = new ConcurrentHashMap<>();

    public void assign(GamePlayer target, GamePlayer owner) {
        if (target == null || owner == null) {
            return;
        }

        colouringTargets.put(target.getUserId(), owner.getUserId());
    }

    public void clear(GamePlayer target) {
        if (target != null) {
            colouringTargets.remove(target.getUserId());
        }
    }

    public void clearAll() {
        colouringTargets.clear();
    }

    public int getColouringForOpponentId(GamePlayer target) {
        if (target == null) {
            return -1;
        }

        return colouringTargets.getOrDefault(target.getUserId(), -1);
    }

    public GameTeam getEffectiveTeam(BattleBallGame game, GamePlayer target) {
        if (target == null || game == null) {
            return null;
        }

        int opponentId = getColouringForOpponentId(target);
        if (opponentId == -1) {
            return game.getTeams().get(target.getTeamId());
        }

        GamePlayer opponent = game.getGamePlayer(opponentId);
        if (opponent == null) {
            return game.getTeams().get(target.getTeamId());
        }

        return game.getTeams().getOrDefault(opponent.getTeamId(), game.getTeams().get(target.getTeamId()));
    }
}
