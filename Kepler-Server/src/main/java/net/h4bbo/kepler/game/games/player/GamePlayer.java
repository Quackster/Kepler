package net.h4bbo.kepler.game.games.player;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.player.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class GamePlayer {
    private Player player;
    private GameObject gameObject;
    private int userId;
    private int objectId;
    private int gameId;
    private int teamId;
    private Position position;
    private boolean enteringGame;
    private boolean isSpectator;
    private boolean inGame;
    private boolean clickedRestart;

    private boolean assignedSpawn;
    private AtomicInteger score;
    private int xp;

    public GamePlayer(Player player) {
        this.player = player;
        this.userId = player.getDetails().getId();
        this.teamId = -1;
        this.gameId = -1;
        this.objectId = -1;
        this.enteringGame = false;
        this.clickedRestart = false;
        this.position = new Position();
        this.score = new AtomicInteger(0);
        this.xp = 0;
    }

    /**
     * Set the score.
     *
     * @param score the score
     */
    public void setScore(int score) {
        this.score.set(score);
    }

    /**
     * Calculate and set the score for this player using the game's score calculator.
     */
    public void calculateScore() {
        if (!this.inGame) {
            this.score.set(0);
            return;
        }

        int calculatedScore = this.getGame().getScoreCalculator().calculatePlayerScore(this);
        this.score.set(calculatedScore);
    }

    public int getScore() {
        return score.get();
    }

    /**
     * Get the xp of the current player
     *
     * @return the score
     */
    public int getXp() {
        return xp;
    }
    /**
     * Sets the xp for the player, does NOT allow negative numbers
     *
     * @param xp the xp to set
     */
    public void setXp(int xp) {
        this.xp = xp;
        if (this.xp < 0) {
            this.xp = 0;
        }
    }


    /**
     * Get the game the game player is currently playing in
     *
     * @return the game instance
     */
    public Game getGame() {
        return GameManager.getInstance().getGameById(this.getGameId());
    }

    /**
     * Get the player being held in this game player instance
     *
     * @return the game player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the user id of the game player
     *
     * @return the user id of the game player
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Get the team id that the player is currently in
     *
     * @return the team id
     */
    public int getTeamId() {
        return teamId;
    }

    /**
     * Set the team id the user is currently in
     *
     * @param teamId the team id
     */
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    /**
     * Set the spawn position of the player, used for when the game starts and restarts
     *
     * @return the spawn position
     */
    public Position getSpawnPosition() {
        return position;
    }

    /**
     * Get the current game id that the gamer player is in, -1 for no game
     *
     * @return the game id
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * Set the game id the user is currently in
     *
     * @param gameId the game id
     */
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    /**
     * Get if the user is entering the game, used for disabling walking on room entry
     *
     * @return true, if successful
     */
    public boolean isEnteringGame() {
        return enteringGame;
    }

    /**
     * Set whether not the users are entering a game
     *
     * @param enteringGame whether not they're entering the game
     */
    public void setEnteringGame(boolean enteringGame) {
        this.enteringGame = enteringGame;
    }

    /**
     * Get whether the user is in game or not
     *
     * @return true, if successful
     */
    public boolean isInGame() {
        return inGame;
    }

    /**
     * Set whether they're in game or not
     *
     * @param inGame the flag to set
     */
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    /**
     * Get if the user has clicked restart when the game has ended
     *
     * @return true, if successful
     */
    public boolean isClickedRestart() {
        return clickedRestart;
    }

    /**
     * Set whether not the gamer player clicked restart when the game ended
     *
     * @param clickedRestart the flag whether or not they clicked restart
     */
    public void setClickedRestart(boolean clickedRestart) {
        this.clickedRestart = clickedRestart;
    }

    /**
     * Get if the user is spectating the match
     *
     * @return true, if successful
     */
    public boolean isSpectator() {
        return isSpectator;
    }

    /**
     * Set whether or not the user is spectating the match or not
     *
     * @param spectator whether or not they're spectating
     */
    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
    }

    /**
     * Get the game object attached to this player
     *
     * @return the game object
     */
    public GameObject getGameObject() {
        return gameObject;
    }

    /**
     * Set the game object attached to this player
     *
     * @param gameObject the game object
     */
    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    /**
     * Get the object id attached to this player
     *
     * @return the object id
     */
    public int getObjectId() {
        return objectId;
    }

    /**
     * Set the object id attached to this player
     *
     * @param objectId the object id
     */
    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public boolean isAssignedSpawn() {
        return assignedSpawn;
    }

    public void setAssignedSpawn(boolean assignedSpawn) {
        this.assignedSpawn = assignedSpawn;
    }

}

