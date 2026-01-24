package net.h4bbo.kepler.game.games.battleball;

import net.h4bbo.kepler.game.games.battleball.enums.BattleBallPlayerState;
import net.h4bbo.kepler.game.games.player.GamePlayer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BattleBallPlayerStateManager {
    private final ConcurrentMap<Integer, BattleBallPlayerState> states = new ConcurrentHashMap<>();

    public BattleBallPlayerState getState(GamePlayer player) {
        if (player == null) {
            return BattleBallPlayerState.NORMAL;
        }

        return states.getOrDefault(player.getUserId(), BattleBallPlayerState.NORMAL);
    }

    public void setState(GamePlayer player, BattleBallPlayerState state) {
        if (player == null || state == null) {
            return;
        }

        states.put(player.getUserId(), state);
    }

    public void remove(GamePlayer player) {
        if (player != null) {
            states.remove(player.getUserId());
        }
    }

    public void reset() {
        states.clear();
    }
}
