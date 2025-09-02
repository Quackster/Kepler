package net.h4bbo.kepler.messages.outgoing.games;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.battleball.BattleBallGame;
import net.h4bbo.kepler.game.games.enums.GameState;
import net.h4bbo.kepler.game.games.enums.GameType;
import net.h4bbo.kepler.game.games.history.GameHistory;
import net.h4bbo.kepler.game.games.history.GameHistoryPlayer;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class GAMEINSTANCE extends MessageComposer {
    private Game game;
    private GameHistory finishedGame;

    public GAMEINSTANCE(Game game) {
        this.game = game;
    }

    public GAMEINSTANCE(GameHistory game) {
        this.finishedGame = game;
    }

    @Override
    public void compose(NettyResponse response) {
        if (this.finishedGame == null) {
            response.writeInt(this.game.getGameState().getStateId());

            if (this.game.getGameState() == GameState.WAITING) {
                response.writeInt(this.game.getId());
                response.writeString(this.game.getName());

                // Host
                response.writeInt(this.game.getGameCreatorId());
                response.writeString(this.game.getGameCreator());

                if (this.game.getGameType() == GameType.SNOWSTORM) {
                    SnowStormGame snowStormGame = (SnowStormGame) this.game;
                    response.writeInt(snowStormGame.getGameLengthChoice());
                }

                response.writeInt(this.game.getMapId());
                response.writeInt(this.game.getSpectators().size());
                response.writeInt(this.game.getTeamAmount());

                for (int i = 0; i < this.game.getTeamAmount(); i++) {
                    List<GamePlayer> playerList = this.game.getTeams().get(i).getPlayers();

                    response.writeInt(playerList.size());

                    for (GamePlayer player : playerList) {
                        response.writeInt(player.getPlayer().getRoomUser().getInstanceId());
                        response.writeString(player.getPlayer().getDetails().getName());
                    }
                }

                if (this.game.getGameType() == GameType.BATTLEBALL) {
                    BattleBallGame battleballGame = (BattleBallGame) this.game;
                    List<Integer> allowedPowerUps = battleballGame.getAllowedPowerUps();

                    String[] powerUps = new String[allowedPowerUps.size()];

                    for (int i = 0; i < allowedPowerUps.size(); i++) {
                        powerUps[i] = String.valueOf(allowedPowerUps.get(i));
                    }

                    response.writeString(String.join(",", powerUps));
                }
            }

            if (this.game.getGameState() == GameState.STARTED) {
                response.writeInt(this.game.getId());
                response.writeString(this.game.getName());
                response.writeString(this.game.getGameCreator());

                if (this.game.getGameType() == GameType.SNOWSTORM) {
                    SnowStormGame snowStormGame = (SnowStormGame) this.game;
                    response.writeInt(snowStormGame.getGameLengthChoice());
                }

                response.writeInt(this.game.getMapId());
                response.writeInt(this.game.getTeamAmount());

                for (int i = 0; i < this.game.getTeamAmount(); i++) {
                    List<GamePlayer> playerList = this.game.getTeams().get(i).getActivePlayers();

                    response.writeInt(playerList.size());

                    for (GamePlayer player : playerList) {
                        //response.writeInt(player.getPlayer().getRoomUser().getInstanceId());
                        response.writeString(player.getPlayer().getDetails().getName());
                    }
                }

                // TODO: Special SnowStorm parameters

                if (this.game.getGameType() == GameType.BATTLEBALL) {
                    BattleBallGame battleballGame = (BattleBallGame) this.game;
                    List<Integer> allowedPowerUps = battleballGame.getAllowedPowerUps();

                    String[] powerUps = new String[allowedPowerUps.size()];

                    for (int i = 0; i < allowedPowerUps.size(); i++) {
                        powerUps[i] = String.valueOf(allowedPowerUps.get(i));
                    }

                    response.writeString(String.join(",", powerUps));
                }

            }
        } else {
            response.writeInt(GameState.ENDED.getStateId());
            response.writeInt(this.finishedGame.getId());
            response.writeString(this.finishedGame.getName());
            response.writeString(this.finishedGame.getGameCreator());

            if (this.finishedGame.getGameType() == GameType.SNOWSTORM) {
                response.writeInt(Integer.valueOf(this.finishedGame.getExtraData()));
            }

            var teamData = this.finishedGame.getHistoryData().getTeamData();

            response.writeInt(this.finishedGame.getMapId());
            response.writeInt(teamData.size());

            for (int i = 0; i < teamData.size(); i++) {
                var players = teamData.get(i);
                var teamScore = players.stream().mapToInt(GameHistoryPlayer::getScore).sum();

                response.writeInt(players.size());

                for (var kvp : players) {
                    response.writeString(kvp.getName());
                    response.writeInt(kvp.getScore());
                }

                response.writeInt(teamScore);
            }

            if (this.finishedGame.getGameType() == GameType.BATTLEBALL) {
                List<Integer> allowedPowerUps = this.finishedGame.getAllowedPowerUps();

                String[] powerUps = new String[allowedPowerUps.size()];

                for (int i = 0; i < allowedPowerUps.size(); i++) {
                    powerUps[i] = String.valueOf(allowedPowerUps.get(i));
                }

                response.writeString(String.join(",", powerUps));
            }
        }
    }

    @Override
    public short getHeader() {
        return 233; // "Ci"
    }
}
