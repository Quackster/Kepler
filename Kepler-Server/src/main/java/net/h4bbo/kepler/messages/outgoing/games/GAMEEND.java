package net.h4bbo.kepler.messages.outgoing.games;

import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.enums.GameType;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.player.GameTeam;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.Map;

public class GAMEEND extends MessageComposer {
    private final GameType gameType;
    private final Map<Integer, GameTeam> teams;

    public GAMEEND(GameType game, Map<Integer, GameTeam> teams) {
        this.gameType = game;
        this.teams = teams;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(GameManager.getInstance().getRestartSeconds(this.gameType));
        response.writeInt(this.teams.size());

        for (GameTeam team : this.teams.values()) {
            var players = team.getPlayers();
            response.writeInt(players.size());

            if (players.size() > 0) {
                for (GamePlayer gamePlayer : players) {
                    response.writeInt(gamePlayer.getObjectId());
                    response.writeString(gamePlayer.getPlayer().getDetails().getName());
                    response.writeInt(gamePlayer.getScore());
                }

                response.writeInt(team.getPoints());
            }
        }
    }

    @Override
    public short getHeader() {
        return 248;
    }
}
