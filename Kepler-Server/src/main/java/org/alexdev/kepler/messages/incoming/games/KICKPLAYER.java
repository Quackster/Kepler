package org.alexdev.kepler.messages.incoming.games;

import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.triggers.GameLobbyTrigger;
import org.alexdev.kepler.messages.outgoing.games.CREATEFAILED;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class KICKPLAYER implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!(room.getModel().getModelTrigger() instanceof GameLobbyTrigger)) {
            return;
        }

        GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();

        if (gamePlayer == null) {
            return;
        }

        Game game = GameManager.getInstance().getGameById(gamePlayer.getGameId());

        if (game == null || game.getGameCreatorId() != player.getDetails().getId()) {
            return;
        }

        int instanceId = reader.readInt();

        GamePlayer teamPlayer = null;

        for (GameTeam team : game.getTeams().values()) {
            for (GamePlayer p : team.getActivePlayers()) {
                if (p.getPlayer().getRoomUser().getInstanceId() == instanceId) {
                    teamPlayer = p;
                    break;
                }
            }
        }

        if (teamPlayer == null) {
            return;
        }

        game.leaveGame(teamPlayer);
        teamPlayer.getPlayer().send(new CREATEFAILED(CREATEFAILED.FailedReason.KICKED));
    }
}
