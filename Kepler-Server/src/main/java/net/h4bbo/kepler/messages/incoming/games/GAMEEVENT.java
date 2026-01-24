package net.h4bbo.kepler.messages.incoming.games;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.battleball.BattleBallGame;
import net.h4bbo.kepler.game.games.battleball.BattleBallPowerUp;
import net.h4bbo.kepler.game.games.battleball.events.ActivatePowerUpEvent;
import net.h4bbo.kepler.game.games.enums.GameType;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.games.snowstorm.messages.SnowStormMessageHandler;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class GAMEEVENT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!player.getRoomUser().isWalkingAllowed()) {
            return;
        }

        GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();

        if (gamePlayer == null) {
            return;
        }

        Game game = gamePlayer.getGame();

        int eventType = reader.readInt(); // Instance ID? Useless?

        if (game.getGameType() == GameType.SNOWSTORM) {
            SnowStormMessageHandler.getInstance().handleMessage(eventType, reader, (SnowStormGame) game, gamePlayer);
        } else {
            // Jump request
            if (eventType == 2) {
                if (!player.getRoomUser().isWalkingAllowed()) {
                    return;
                }

                int X = reader.readInt();
                int Y = reader.readInt();

                player.getRoomUser().walkTo(X, Y);
            }

            // Use power up request
            if (eventType == 4) {
                int powerId = reader.readInt();

                if (game instanceof BattleBallGame) {
                    BattleBallGame battleballGame = (BattleBallGame) game;

                    if (!battleballGame.getStoredPowers().containsKey(gamePlayer)) {
                        return;
                    }

                    var powerList = battleballGame.getStoredPowers().get(gamePlayer);

                    BattleBallPowerUp powerUp = null;

                    for (BattleBallPowerUp power : powerList) {
                        if (power.getId() == powerId) {
                            powerUp = power;
                            break;
                        }
                    }

                    if (powerUp != null) {
                        battleballGame.getEventsQueue().add(new ActivatePowerUpEvent(gamePlayer, powerUp));
                        powerList.remove(powerUp);

                        powerUp.usePower(gamePlayer, player.getRoomUser().getPosition());
                    }
                }
            }
        }
    }
}
