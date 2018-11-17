package org.alexdev.kepler.messages.incoming.games;

import org.alexdev.kepler.game.games.utils.FinishedGame;
import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.enums.GameState;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.triggers.GameLobbyTrigger;
import org.alexdev.kepler.messages.outgoing.games.GAMEINSTANCE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class OBSERVEINSTANCE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!(room.getModel().getModelTrigger() instanceof GameLobbyTrigger)) {
            return;
        }

        int gameId = reader.readInt();

        Game game = GameManager.getInstance().getGameById(gameId);

        if (game != null && game.getGameState() != GameState.ENDED) {
            player.send(new GAMEINSTANCE(game));
            player.getRoomUser().setObservingGameId(gameId);

            game.getObservers().add(player);
            return;
        }

        FinishedGame finishedGame = GameManager.getInstance().getFinishedGameById(gameId);

        if (finishedGame != null) {
            player.send(new GAMEINSTANCE(finishedGame));
        }

        /*if (game.getGameState() == GameState.WAITING) {
            if (player.getDetails().getTickets() <= 1) {
                player.send(new JOINFAILED(JOINFAILED.FailedReason.TICKETS_NEEDED));
                return;
            }

            // Find team with lowest team members to add to
            List<GameTeam> sortedTeamList = new ArrayList<>(game.getTeams().values());
            sortedTeamList.sort(Comparator.comparingInt(team -> team.getPlayers().size()));

            // Select game team
            GameTeam gameTeam = sortedTeamList.get(0);

            if (gameTeam == null) {
                return;
            }

            if (!game.canSwitchTeam(gameTeam.getId())) {
                return;
            }

            player.getRoomUser().setGamePlayer(new GamePlayer(player));
            player.getRoomUser().getGamePlayer().setGameId(game.getId());
            player.getRoomUser().getGamePlayer().setTeamId(gameTeam.getId());

            game.movePlayer(player.getRoomUser().getGamePlayer(), -1, gameTeam.getId());
            game.send(new GAMEINSTANCE(game));
        }*/


    }
}
