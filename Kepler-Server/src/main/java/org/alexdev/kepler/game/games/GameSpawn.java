package org.alexdev.kepler.game.games;

import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.game.pathfinder.Position;

public class GameSpawn extends Position {
    private int teamId;
    private int mapId;
    private GameType gameType;

    public GameSpawn(int teamId, int mapId, String gameType, int x, int y, int z) {
        super(x, y, 0, z, z);
        this.teamId = teamId;
        this.mapId = mapId;
        this.gameType = GameType.valueOf(gameType.toUpperCase());
    }

    public int getTeamId() {
        return teamId;
    }

    public int getMapId() {
        return mapId;
    }

    public GameType getGameType() {
        return gameType;
    }
}
