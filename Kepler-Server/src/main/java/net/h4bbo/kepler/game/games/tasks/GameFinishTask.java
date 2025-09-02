package net.h4bbo.kepler.game.games.tasks;

import net.h4bbo.kepler.Kepler;
import net.h4bbo.kepler.dao.mysql.GameDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.GameScheduler;
import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.enums.GameType;
import net.h4bbo.kepler.game.games.history.GameHistory;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.player.GameTeam;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.game.room.managers.RoomTradeManager;
import net.h4bbo.kepler.server.netty.NettyPlayerNetwork;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GameFinishTask implements Runnable {
    private final List<GamePlayer> players;
    private final List<GameTeam> sortedTeamList;
    private final GameType gameType;
    private final Game game;
    private final GameHistory gameHistory;

    public GameFinishTask(Game game, GameHistory gameHistory, GameType gameType, List<GameTeam> sortedTeamList, List<GamePlayer> players) {
        this.game = game;
        this.gameHistory = gameHistory;
        this.gameType = gameType;
        this.sortedTeamList = sortedTeamList;
        this.players = players;
    }


    @Override
    public void run() {
        boolean saveScore = false;

        GameManager.getInstance().getFinishedGameCounter().incrementAndGet();

        if (this.game.canIncreasePoints()) {
            for (GamePlayer gamePlayer : this.game.getActivePlayers()) {
                gamePlayer.getScore();

                if (this.gameType == GameType.BATTLEBALL) {
                    gamePlayer.setXp(gamePlayer.getScore() > 0 ? (gamePlayer.getScore() / 294) : 0);
                } else {
                    gamePlayer.setXp(gamePlayer.getScore() > 0 ? (gamePlayer.getScore() / 3) : 0);
                }
            }
        }

        if (this.game.canIncreasePoints()) {
            if ((this.sortedTeamList.size() == 1 && this.sortedTeamList.get(0).getPlayers().size() > 1) || (this.sortedTeamList.size() > 1
                    && this.sortedTeamList.get(0).getPoints() > 0
                    && this.sortedTeamList.get(0).getPoints() != this.sortedTeamList.get(1).getPoints()) &&
                    this.sortedTeamList.get(0).getPlayers().size() > 0 &&
                    this.sortedTeamList.get(1).getPlayers().size() > 0) {

                saveScore = true;


                Player firstPlayer = null;//this.sortedTeamList.get(0).getPlayers().get(0).getPlayer();
                Player secondPlayer = null;//this.sortedTeamList.get(1).getPlayers().get(0).getPlayer();


                if (this.sortedTeamList.size() == 1) {
                    firstPlayer = this.sortedTeamList.get(0).getPlayers().get(0).getPlayer();
                    secondPlayer = this.sortedTeamList.get(0).getPlayers().get(1).getPlayer();
                } else {
                    firstPlayer = this.sortedTeamList.get(0).getPlayers().get(0).getPlayer();
                    secondPlayer = this.sortedTeamList.get(1).getPlayers().get(0).getPlayer();
                }

                String firstIp = NettyPlayerNetwork.getIpAddress(firstPlayer.getNetwork().getChannel());
                String secondIp = NettyPlayerNetwork.getIpAddress(secondPlayer.getNetwork().getChannel());

                /*
                if (!firstIp.equals(secondIp)
                        && !PlayerDao.getIpAddresses(firstPlayer.getDetails().getId(), RoomTradeManager.TRADE_BAN_IP_HISTORY_LIMIT).contains(secondIp)
                        && !PlayerDao.getIpAddresses(secondPlayer.getDetails().getId(), RoomTradeManager.TRADE_BAN_IP_HISTORY_LIMIT).contains(firstIp)) {
                    for (GamePlayer g : this.sortedTeamList.get(0).getPlayers()) {
                        Player winningPlayer = g.getPlayer();

                        if (this.gameType == GameType.BATTLEBALL) {
                            winningPlayer.getStatisticManager().incrementValue(PlayerStatistic.BATTLEBALL_GAMES_WON, 1);
                        } else {
                            winningPlayer.getStatisticManager().incrementValue(PlayerStatistic.SNOWSTORM_GAMES_WON, 1);
                        }

                        // AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_GAME_PLAYED, winningPlayer);
                    }
                } else {
                    saveScore = false;
                }*/
            }

            for (GamePlayer gamePlayer : this.players) {
                var setMap = new HashMap<PlayerStatistic, String>();

                if (saveScore) {
                    var score = (long) gamePlayer.getScore();

                    if (this.gameType == GameType.BATTLEBALL) {
                        setMap.put(PlayerStatistic.BATTLEBALL_MONTHLY_SCORES, String.valueOf(gamePlayer.getPlayer().getStatisticManager().getLongValue(PlayerStatistic.BATTLEBALL_MONTHLY_SCORES) + score));
                        setMap.put(PlayerStatistic.BATTLEBALL_POINTS_ALL_TIME, String.valueOf(gamePlayer.getPlayer().getStatisticManager().getLongValue(PlayerStatistic.BATTLEBALL_POINTS_ALL_TIME) + score));
                    } else {
                        setMap.put(PlayerStatistic.SNOWSTORM_MONTHLY_SCORES, String.valueOf(gamePlayer.getPlayer().getStatisticManager().getLongValue(PlayerStatistic.SNOWSTORM_MONTHLY_SCORES) + score));
                        setMap.put(PlayerStatistic.SNOWSTORM_POINTS_ALL_TIME, String.valueOf(gamePlayer.getPlayer().getStatisticManager().getLongValue(PlayerStatistic.SNOWSTORM_POINTS_ALL_TIME) + score));
                    }

                    var player = gamePlayer.getPlayer();

                    setMap.put(PlayerStatistic.XP_EARNED_MONTH, String.valueOf(gamePlayer.getPlayer().getStatisticManager().getLongValue(PlayerStatistic.XP_EARNED_MONTH) + (long) gamePlayer.getXp()));
                    setMap.put(PlayerStatistic.XP_ALL_TIME, String.valueOf(gamePlayer.getPlayer().getStatisticManager().getLongValue(PlayerStatistic.XP_ALL_TIME) + (long) gamePlayer.getXp()));
                    player.getStatisticManager().setValues(gamePlayer.getUserId(), setMap);

                    if (gamePlayer.getXp() > 0) {
                        int creditsAmount = GameManager.getInstance().getRandomCredits(this.sortedTeamList.get(0).getPlayers().contains(gamePlayer));

                        if (creditsAmount > 0) {
                            GameScheduler.getInstance().queuePlayerCredits(player, creditsAmount);
                        }
                    }
                }
            }

            if (saveScore) {
                String uniqueId = UUID.randomUUID().toString();
                GameDao.saveTeamHistory(uniqueId, this.gameHistory.getName(), this.game.getGameCreatorId(), this.gameHistory.getMapId(), this.gameHistory.getWinningTeam(), this.gameHistory.getWinningTeamScore(), this.gameHistory.getExtraData(), this.gameType, Kepler.getGson().toJson(this.gameHistory.getGameHistoryData()));
                GameManager.getInstance().refreshPlayedGames();
            }

                /*for (GameTeam team : this.sortedTeamList) {
                    if (team.getPlayers().isEmpty()) {
                        continue;
                    }

                    GameHistoryData gamePlayedHistory = new GameHistoryData();

                    for (GamePlayer gamePlayer : team.getPlayers()) {
                        gamePlayedHistory.addPlayer(gamePlayer.getPlayer().getDetails().getId(), gamePlayer.calculateScore());
                    }

                    GameDao.saveTeamHistory(uniqueId, this.gameType, team.getId(), team.getScore(), Havana.getGson().toJson(gamePlayedHistory));
                }*/
            }
        }

        /*var sortedPlayers = new ArrayList<>(this.game.getPlayers());
        sortedPlayers.sort(Comparator.comparingInt(GamePlayer::getScore).reversed());

        boolean firstTeamHasHighscore = this.sortedTeamList.size() > 0 && this.sortedTeamList.get(0).hasHighscore();
        boolean firstPlayerHasHighscore = sortedPlayers.size() > 0 && sortedPlayers.get(0).hasHighscore();

        // Only set highscore for top team
        if (this.sortedTeamList.size() > 0) {
            for (var team : this.sortedTeamList) {
                team.setHasHighscore(false);
            }

            this.sortedTeamList.get(0).setHasHighscore(firstTeamHasHighscore);
        }

        // Only set highscore for top player
        if (this.sortedTeamList.size() > 0) {
            for (var player : sortedPlayers) {
                player.setHasHighscore(false);
            }

            sortedPlayers.get(0).setHasHighscore(firstPlayerHasHighscore);
        }*/

        /*this.game.send(new GAME_ENDING(this.game, this.sortedTeamList,
                GameManager.getInstance().getTopPlayers(),
                GameManager.getInstance().getTopTeams()));*/
}
