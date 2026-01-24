package net.h4bbo.kepler.game.games.utils;

import net.h4bbo.kepler.game.GameScheduler;
import net.h4bbo.kepler.game.games.battleball.BattleBallGame;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallPlayerState;
import net.h4bbo.kepler.game.games.battleball.objects.PlayerUpdateObject;
import net.h4bbo.kepler.game.games.enums.GameState;
import net.h4bbo.kepler.game.games.player.GamePlayer;

import java.util.concurrent.TimeUnit;

public class PowerUpUtil {
    public static void stunPlayer(BattleBallGame game, GamePlayer gamePlayer, BattleBallPlayerState state) {
        gamePlayer.getPlayer().getRoomUser().setWalkingAllowed(false);
        gamePlayer.getPlayer().getRoomUser().stopWalking();

        game.getPlayerStateManager().setState(gamePlayer, state);
        game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));

        // Restore player 5 seconds later
        GameScheduler.getInstance().getService().schedule(()-> {
            if (game.getGameState() != GameState.ENDED) {
                gamePlayer.getPlayer().getRoomUser().setWalkingAllowed(true);
            }

            game.getPlayerStateManager().setState(gamePlayer, BattleBallPlayerState.NORMAL);
            game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));
        }, 5, TimeUnit.SECONDS);
    }

}
