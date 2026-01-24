package org.alexdev.kepler.messages.incoming.games;

import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormPlayers;
import org.alexdev.kepler.game.games.snowstorm.objects.SnowStormAvatarObject;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormAttributes;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.games.FULLGAMESTATUS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class REQUESTFULLGAMESTATUS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();

        if (gamePlayer == null) {
            return;
        }

        Game game = GameManager.getInstance().getGameById(gamePlayer.getGameId());

        if (game instanceof SnowStormGame) {
            var snowStormAvatarObject = SnowStormAvatarObject.getAvatar(gamePlayer);
            var attributes = SnowStormPlayers.get(gamePlayer);

            if (snowStormAvatarObject == null || attributes == null) {
                return;
            }

            if (attributes.isWalking()) {
                snowStormAvatarObject.stopWalking();
            }

            player.send(new FULLGAMESTATUS(game));
        }
    }
}
