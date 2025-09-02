package net.h4bbo.kepler.messages.incoming.games;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.enums.GameState;
import net.h4bbo.kepler.game.games.history.GameHistory;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.triggers.GameLobbyTrigger;
import net.h4bbo.kepler.messages.outgoing.games.GAMEINSTANCE;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class OBSERVEINSTANCE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!(room.getModel().getRoomTrigger() instanceof GameLobbyTrigger)) {
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

        var lobbyTrigger = (GameLobbyTrigger)room.getModel().getRoomTrigger();
        GameHistory finishedGame = GameManager.getInstance().getFinishedGameById(lobbyTrigger.getGameType(), gameId);

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
