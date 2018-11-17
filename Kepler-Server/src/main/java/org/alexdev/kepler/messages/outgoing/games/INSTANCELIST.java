package org.alexdev.kepler.messages.outgoing.games;

import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.utils.FinishedGame;
import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.enums.GameState;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;
import java.util.stream.Collectors;

public class INSTANCELIST extends MessageComposer {
    private final List<Game> createdGames;
    private final List<Game> startedGames;
    private final List<FinishedGame> finishedGames;

    public INSTANCELIST(List<Game> gamesByType, List<FinishedGame> finishedGames) {
        this.createdGames = gamesByType.stream().filter(game -> game.getGameState() == GameState.WAITING).collect(Collectors.toList());
        this.startedGames = gamesByType.stream().filter(game -> game.getGameState() == GameState.STARTED).collect(Collectors.toList());
        this.finishedGames = finishedGames;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.createdGames.size());

        for (Game game : this.createdGames) {
            response.writeInt(game.getId());
            response.writeString(game.getName());

            response.writeInt(game.getGameCreatorId());
            response.writeString(game.getGameCreator());

            if (game.getGameType() == GameType.SNOWSTORM) {
                SnowStormGame snowStormGame = (SnowStormGame) game;
                response.writeInt(snowStormGame.getGameLengthChoice());
            }

            response.writeInt(game.getMapId());
        }

        response.writeInt(this.startedGames.size());

        for (Game game : this.startedGames) {
            response.writeInt(game.getId());
            response.writeString(game.getName());
            response.writeString(game.getGameCreator());

            if (game.getGameType() == GameType.SNOWSTORM) {
                SnowStormGame snowStormGame = (SnowStormGame) game;
                response.writeInt(snowStormGame.getGameLengthChoice());
            }

            response.writeInt(game.getMapId());
        }

        response.writeInt(this.finishedGames.size());

        for (FinishedGame game : this.finishedGames) {
            response.writeInt(game.getId());
            response.writeString(game.getName());
            response.writeString(game.getMapCreator());

            if (game.getGameType() == GameType.SNOWSTORM) {
                //SnowStormGame snowStormGame = (SnowStormGame) this.game;
                response.writeInt(0);//snowStormGame.getGameLengthChoice());
            }

            response.writeInt(game.getMapId());
        }
    }

    @Override
    public short getHeader() {
        return 232; // "Ch"
    }
}
