package net.h4bbo.kepler.game.games.battleball.powerups;

import net.h4bbo.kepler.game.GameScheduler;
import net.h4bbo.kepler.game.games.battleball.BattleBallGame;
import net.h4bbo.kepler.game.games.battleball.BattleBallPlayerStateManager;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallPlayerState;
import net.h4bbo.kepler.game.games.battleball.objects.PlayerUpdateObject;
import net.h4bbo.kepler.game.games.enums.GameState;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.room.Room;

import java.util.concurrent.TimeUnit;

public class SpringHandle {
    public static void handle(BattleBallGame game, GamePlayer gamePlayer, Room room) {
        BattleBallPlayerStateManager stateManager = game.getPlayerStateManager();
        stateManager.setState(gamePlayer, BattleBallPlayerState.HIGH_JUMPS);
        game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));

        GameScheduler.getInstance().getService().schedule(()-> {
            if (game.getGameState() == GameState.ENDED) {
                return;
            }

            if (stateManager.getState(gamePlayer) != BattleBallPlayerState.HIGH_JUMPS) {
                return;
            }

            stateManager.setState(gamePlayer, BattleBallPlayerState.NORMAL);
            game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));
        }, 10, TimeUnit.SECONDS);
    }
}
