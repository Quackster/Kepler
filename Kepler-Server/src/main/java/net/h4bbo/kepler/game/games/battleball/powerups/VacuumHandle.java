package org.alexdev.kepler.game.games.battleball.powerups;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.games.battleball.BattleBallGame;
import org.alexdev.kepler.game.games.battleball.BattleBallPlayerStateManager;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallPlayerState;
import org.alexdev.kepler.game.games.battleball.objects.PlayerUpdateObject;
import org.alexdev.kepler.game.games.enums.GameState;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.room.Room;

import java.util.concurrent.TimeUnit;

public class VacuumHandle {
    public static void handle(BattleBallGame game, GamePlayer gamePlayer, Room room) {
        BattleBallPlayerStateManager stateManager = game.getPlayerStateManager();
        stateManager.setState(gamePlayer, BattleBallPlayerState.CLEANING_TILES);
        game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));

        GameScheduler.getInstance().getService().schedule(() -> {
            if (game.getGameState() == GameState.ENDED) {
                return;
            }

            if (stateManager.getState(gamePlayer) != BattleBallPlayerState.CLEANING_TILES) {
                return;
            }

            stateManager.setState(gamePlayer, BattleBallPlayerState.NORMAL);
            game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));
        }, 10, TimeUnit.SECONDS);
    }
}
