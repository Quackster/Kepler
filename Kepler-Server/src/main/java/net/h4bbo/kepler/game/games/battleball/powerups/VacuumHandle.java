package net.h4bbo.kepler.game.games.battleball.powerups;

import net.h4bbo.kepler.game.GameScheduler;
import net.h4bbo.kepler.game.games.battleball.BattleBallGame;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallPlayerState;
import net.h4bbo.kepler.game.games.battleball.objects.PlayerUpdateObject;
import net.h4bbo.kepler.game.games.enums.GameState;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.room.Room;

import java.util.concurrent.TimeUnit;

public class VacuumHandle {
    public static void handle(BattleBallGame game, GamePlayer gamePlayer, Room room) {
        gamePlayer.setPlayerState(BattleBallPlayerState.CLEANING_TILES);
        game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));

        GameScheduler.getInstance().getService().schedule(() -> {
            if (game.getGameState() == GameState.ENDED) {
                return;
            }

            if (gamePlayer.getPlayerState() != BattleBallPlayerState.CLEANING_TILES) {
                return;
            }

            gamePlayer.setPlayerState(BattleBallPlayerState.NORMAL);
            game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));
        }, 10, TimeUnit.SECONDS);
    }
}
