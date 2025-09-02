package net.h4bbo.kepler.messages.incoming.games;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.games.PLAYERREJOINED;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.List;

public class GAMERESTART implements MessageEvent {
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

        if (game == null || !game.isGameFinished() || gamePlayer.isClickedRestart()) {
            return;
        }

        // Only allow restart once everyone has clicked they'd like to restart
        gamePlayer.setClickedRestart(true);
        game.send(new PLAYERREJOINED(player.getRoomUser().getInstanceId()));

        List<GamePlayer> players = new ArrayList<>(); // Players who wanted to restart
        List<GamePlayer> afkPlayers = new ArrayList<>(); // Players who didn't touch any button

        for (GamePlayer p : game.getActivePlayers()) {
            if (!p.isClickedRestart()) {
                afkPlayers.add(p);
            } else {
                players.add(p);
            }
        }

        if (afkPlayers.isEmpty()) { // Everyone clicked restart
            for (GamePlayer p : game.getActivePlayers()) {
                p.setClickedRestart(false); // Reset whether or not they clicked restart, for next game
            }

            game.restartGame(players);
        }


/*        for (GameTeam gameTeam : game.getTeams().values()) {
            for (GamePlayer p : gameTeam.getActivePlayers()) {
                if (!p.isClickedRestart()) {
                    return;
                }
            }

        }*/
        /*Game restartGame = new Game(game.getId(), game.getMapId(), game.getGameType(), game.getName(), game.getTeamAmount(), game.getGameCreator().getDetails().getId());

        List<GamePlayer> restartedPlayers = new ArrayList<>();

        for (var gameUser : newPlayers) {
            var gp = new GamePlayer(gameUser.getPlayer());
            gp.setGameId(game.getId());
            gp.setTeamId(gameUser.getTeamId());
            gp.setInGame(true);

            gameUser.getPlayer().getRoomUser().setGamePlayer(gp);

            game.leaveGame(gameUser);
            restartGame.movePlayer(gp, -1, gp.getTeamId());

            restartedPlayers.add(gp);
        }

        restartGame.assignSpawnPoints();*/

    }
}
