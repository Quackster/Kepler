package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.game.games.enums.GameType;

public class GameSpawn {
    private int teamId;
    private int mapId;
    private GameType gameType;
    private int x;
    private int y;
    private int z;

    public GameSpawn(int teamId, int mapId, String gameType, int x, int y, int z) {
        this.teamId = teamId;
        this.mapId = mapId;
        this.gameType = GameType.valueOf(gameType.toUpperCase());
        this.x = x;
        this.y = y;
        this.z = z;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
