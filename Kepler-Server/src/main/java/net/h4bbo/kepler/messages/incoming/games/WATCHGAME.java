package net.h4bbo.kepler.messages.incoming.games;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.enums.GameState;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.triggers.GameLobbyTrigger;
import net.h4bbo.kepler.messages.outgoing.games.GAMEINSTANCE;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class WATCHGAME implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!(room.getModel().getRoomTrigger() instanceof GameLobbyTrigger)) {
            return;
        }

        if (player.getRoomUser().getGamePlayer() != null) {
            return;
        }

        int gameId = reader.readInt();

        Game game = GameManager.getInstance().getGameById(gameId);

        if (game == null) {
            return;
        }

        GamePlayer gamePlayer = new GamePlayer(player);
        gamePlayer.setGameId(gameId);
        gamePlayer.setSpectator(true);

        player.getRoomUser().setGamePlayer(gamePlayer);

        game.getSpectators().add(gamePlayer);

        if (game.getGameState() == GameState.STARTED) {
            game.sendSpectatorToArena(gamePlayer);
        } else {
            game.send(new GAMEINSTANCE(game));
            game.sendObservers(new GAMEINSTANCE(game));
        }
    }
}
