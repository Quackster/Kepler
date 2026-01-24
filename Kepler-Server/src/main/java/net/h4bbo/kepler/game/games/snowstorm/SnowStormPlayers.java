package net.h4bbo.kepler.game.games.snowstorm;

import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormAttributes;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Central registry for SnowStorm-specific player attributes so the core {@link GamePlayer}
 * class stays agnostic.
 */
public final class SnowStormPlayers {
    private static final ConcurrentMap<Integer, SnowStormAttributes> ATTRIBUTES = new ConcurrentHashMap<>();

    private SnowStormPlayers() { }

    public static SnowStormAttributes get(GamePlayer player) {
        Objects.requireNonNull(player, "player");
        return ATTRIBUTES.computeIfAbsent(player.getUserId(), id -> new SnowStormAttributes());
    }

    public static void remove(GamePlayer player) {
        if (player != null) {
            ATTRIBUTES.remove(player.getUserId());
        }
    }

    public static void reset() {
        ATTRIBUTES.clear();
    }
}
