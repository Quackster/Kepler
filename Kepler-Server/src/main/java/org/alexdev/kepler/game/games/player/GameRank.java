package org.alexdev.kepler.game.games.player;

import org.alexdev.kepler.game.games.enums.GameType;

public class GameRank {
    private final int id;
    private final GameType type;
    private final String title;
    private final int minPoints;
    private final int maxPoints;

    public GameRank(int id, String type, String title, int minPoints, int maxPoints) {
        this.id = id;
        this.type = GameType.valueOf(type.toUpperCase());
        this.title = title;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
    }

    /**
     * Get the game rank ID
     *
     * @return the game rank ID
     */
    public int getId() {
        return id;
    }

    /**
     * Get the game type for this rank
     *
     * @return the game type
     */
    public GameType getType() {
        return type;
    }

    /**
     * Get the rank title
     *
     * @return the rank title of this game
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the minimum amount of points required for this game
     *
     * @return the minimum points
     */
    public int getMinPoints() {
        return minPoints;
    }

    /**
     * Get the maximum amount of points required for this game
     *
     * @return the maximum amount of points
     */
    public int getMaxPoints() {
        return maxPoints;
    }
}
