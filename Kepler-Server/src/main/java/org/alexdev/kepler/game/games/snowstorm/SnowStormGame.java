package org.alexdev.kepler.game.games.snowstorm;

import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.GameTile;
import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.game.player.Player;

public class SnowStormGame extends Game {
    public SnowStormGame(int id, int mapId, String name, int teamAmount, Player gameCreator, int gameLengthChoice, boolean privateGame) {
        super(id, mapId, GameType.SNOWSTORM, name, teamAmount, gameCreator);
    }

    @Override
    public void initialise() { }

    @Override
    public boolean hasEnoughPlayers() {
        return false;
    }

    @Override
    public void assignSpawnPoints() { }

    @Override
    public void gameTick() { }

    @Override
    public boolean canTimerContinue() {
        return true;
    }

    @Override
    public GameTile[][] getTileMap() {
        return new GameTile[0][];
    }

    @Override
    public void buildMap() { }
}
