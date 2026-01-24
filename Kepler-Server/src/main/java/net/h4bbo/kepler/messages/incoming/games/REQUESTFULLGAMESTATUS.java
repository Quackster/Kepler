package net.h4bbo.kepler.messages.incoming.games;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormPlayers;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormAvatarObject;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormAttributes;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.games.FULLGAMESTATUS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
