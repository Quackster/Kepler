package net.h4bbo.kepler.messages.incoming.games;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.enums.GameState;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.triggers.GameLobbyTrigger;
import net.h4bbo.kepler.messages.outgoing.games.JOINFAILED;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class INITIATEJOINGAME implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        int instanceId = reader.readInt();
        int teamId = reader.readInt();

        if (!(room.getModel().getRoomTrigger() instanceof GameLobbyTrigger)) {
            return;
        }

        /*GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();

        if (gamePlayer == null) {
            return;
        }*/

        Game game = GameManager.getInstance().getGameById(instanceId);

        if (game == null || game.getGameState() != GameState.WAITING) {
            return;
        }

        if (player.getDetails().getTickets() < game.getTicketCost()) {
            player.send(new JOINFAILED(JOINFAILED.FailedReason.TICKETS_NEEDED, null));
            return;
        }

        if (!game.canSwitchTeam(teamId)) {
            player.send(new JOINFAILED(JOINFAILED.FailedReason.TEAMS_FULL, "join"));
            return;
        }

        // If player was initially a spectator, they need to leave
        if (player.getRoomUser().getGamePlayer() != null &&
            player.getRoomUser().getGamePlayer().isSpectator()) {
            game.leaveGame(player.getRoomUser().getGamePlayer());
        }

        // Their game player instance will always be null after leaveGame()
        if (player.getRoomUser().getGamePlayer() == null) {
            player.getRoomUser().setGamePlayer(new GamePlayer(player));
            player.getRoomUser().getGamePlayer().setGameId(game.getId());
            player.getRoomUser().getGamePlayer().setTeamId(teamId);
        }

        GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();
        game.getObservers().remove(player); // Player was a viewer
        game.movePlayer(gamePlayer, gamePlayer.getTeamId(), teamId);
    }
}
