package net.h4bbo.kepler.messages.incoming.games;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.enums.GameState;
import net.h4bbo.kepler.game.games.enums.GameType;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.triggers.GameLobbyTrigger;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;
import net.h4bbo.kepler.messages.outgoing.games.STARTFAILED;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class STARTGAME implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {

        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!(room.getModel().getRoomTrigger() instanceof GameLobbyTrigger)) {
            return;
        }

        GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();

        if (gamePlayer == null) {
            return;
        }

        Game game = GameManager.getInstance().getGameById(gamePlayer.getGameId());

        if (game.getGameState() != GameState.WAITING) {
            return;
        }

        if (game.getGameCreatorId() != player.getDetails().getId()) {
            return;
        }

        if (!game.canGameStart()) {
            if (game.getGameType() == GameType.SNOWSTORM && game.getTeamAmount() == 1) {
                player.send(new ALERT("There needs to be at least two players to start this match"));
            } else {
                player.send(new STARTFAILED(STARTFAILED.FailedReason.MINIMUM_TEAMS_REQUIRED, null));
            }
            return;
        }

        game.startGame();
    }
}