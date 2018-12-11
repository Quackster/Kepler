package org.alexdev.kepler.game.games.battleball.powerups;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.games.battleball.BattleBallGame;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallPlayerState;
import org.alexdev.kepler.game.games.battleball.objects.PlayerUpdateObject;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.room.Room;

import java.util.concurrent.TimeUnit;

public class SpringHandle {
    public static void handle(BattleBallGame game, GamePlayer gamePlayer, Room room) {
        gamePlayer.setPlayerState(BattleBallPlayerState.HIGH_JUMPS);
        game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));

        GameScheduler.getInstance().getSchedulerService().schedule(()-> {
            if (game.isGameFinished()) {
                return;
            }

            if (gamePlayer.getPlayerState() != BattleBallPlayerState.HIGH_JUMPS) {
                return;
            }

            gamePlayer.setPlayerState(BattleBallPlayerState.NORMAL);
            game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));
        }, 10, TimeUnit.SECONDS);
    }
}
