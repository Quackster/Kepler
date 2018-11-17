package org.alexdev.kepler.game.games.battleball;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.games.GameEvent;
import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.GameTile;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallColourState;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallPlayerState;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallPowerType;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallTileState;
import org.alexdev.kepler.game.games.battleball.events.AcquirePowerUpEvent;
import org.alexdev.kepler.game.games.battleball.events.DespawnObjectEvent;
import org.alexdev.kepler.game.games.battleball.objects.PinObject;
import org.alexdev.kepler.game.games.battleball.objects.PowerUpUpdateObject;
import org.alexdev.kepler.game.games.battleball.powerups.NailBoxHandle;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;
import org.alexdev.kepler.game.games.utils.FloodFill;
import org.alexdev.kepler.game.games.utils.PowerUpUtil;
import org.alexdev.kepler.game.games.utils.TileUtil;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.mapping.RoomTile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BattleBallTile extends GameTile  {
    private BattleBallColourState colour;
    private BattleBallTileState state;

    public BattleBallTile(Position position) {
        super(position);
    }

    public List<GamePlayer> getPlayers(GamePlayer gamePlayer) {
        RoomTile tile = gamePlayer.getGame().getRoom().getMapping().getTile(this.getPosition().getX(), this.getPosition().getY());

        List<GamePlayer> gamePlayers = new ArrayList<>();

        for (Entity entity : tile.getEntities()) {
            if (entity.getType() != EntityType.PLAYER/* || entity.getDetails().getId()== gamePlayer.getUserId()*/) {
                continue;
            }

            Player player = (Player) entity;
            GamePlayer gameUser = player.getRoomUser().getGamePlayer();

            if (gameUser == null) {
                continue;
            }

            if (gamePlayers.contains(gameUser)) {
                continue;
            }

            gamePlayers.add(gameUser);
        }

        return gamePlayers;
    }

    /**
     * Handle when a player jumps on a Battleball tile.
     * @param gamePlayer the GamePlayer instance of the user jumping on the tile
     * @param objects
     * @param updateTiles the tile list to add to if the tile requires an update
     * @param updateFillTiles the list to add to if these tiles require the filling in animation
     */
    public void interact(GamePlayer gamePlayer, List<GameObject> objects, List<GameEvent> events, List<BattleBallTile> updateTiles, List<BattleBallTile> updateFillTiles) {
        BattleBallPowerUp.checkPowerUp(this, gamePlayer, objects, events);

        if (NailBoxHandle.checkNailTile(gamePlayer)) {
            return;
        }

        if (BattleBallPowerUp.hasUsedPower(this, gamePlayer, updateTiles, updateFillTiles)) {
            return;
        }

        this.changeState(gamePlayer, updateTiles, updateFillTiles);
    }

    private void changeState(GamePlayer gamePlayer, List<BattleBallTile> updateTiles, List<BattleBallTile> updateFillTiles) {
        if (this.getColour() == BattleBallColourState.DISABLED) {
            return;
        }

        BattleBallTileState state = this.getState();
        BattleBallColourState colour = this.getColour();

        GameTeam team = gamePlayer.getTeam();

        if (colour == BattleBallColourState.DISABLED) {
            return;
        }

        if (state != BattleBallTileState.SEALED) {
            if (colour.getColourId() == team.getId()) {
                this.setState(BattleBallTileState.getStateById(state.getTileStateId() + 1));
            } else {
                if (gamePlayer.getGame().getMapId() == 5) { // Barebones classic takes 4 hits
                    this.setState(BattleBallTileState.TOUCHED);
                    this.setColour(BattleBallColourState.getColourById(team.getId()));
                } else {
                    this.setState(BattleBallTileState.CLICKED);
                    this.setColour(BattleBallColourState.getColourById(team.getId()));
                }
            }

            BattleBallTileState newState = this.getState();
            BattleBallColourState newColour = this.getColour();

            getNewPoints(gamePlayer, state, colour, newState, newColour);
            checkFill(gamePlayer, this, updateFillTiles);

            updateTiles.add(this);
        }
    }

    public static int getNewPoints(GamePlayer gamePlayer, BattleBallTileState state, BattleBallColourState colour, BattleBallTileState newState, BattleBallColourState newColour) {
        GameTeam team = gamePlayer.getTeam();

        int newPoints = -1;
        boolean sealed = false;

        if (state != newState && newState == BattleBallTileState.TOUCHED) {
            newPoints = 2;

            if (colour != newColour) {
                newPoints = 4;
            }
        }

        if (state != newState && newState == BattleBallTileState.CLICKED) {
            newPoints = 6;

            if (colour != newColour) {
                newPoints = 8;
            }
        }

        if (state != newState && newState == BattleBallTileState.PRESSED) {
            newPoints = 10;

            if (colour != newColour) {
                newPoints = 12;
            }
        }

        if (state != newState && newState == BattleBallTileState.SEALED) {
            newPoints = 14;
            sealed = true;
        }


        if (newPoints != -1) {
            if (!sealed) { // Set to sealed
                // Increase score for other team if harlequin is enabled
                if (gamePlayer.getHarlequinPlayer() != null) {
                    int pointsAcrossTeams = newPoints / team.getActivePlayers().size();

                    for (GamePlayer p : team.getActivePlayers()) {
                        p.setScore(p.getScore() + pointsAcrossTeams);
                    }
                } else {
                    gamePlayer.setScore(gamePlayer.getScore() + newPoints);
                }
            } else {
                // Tile got sealed, so increase every team members' points
                team.setSealedTileScore();
            }
        }

        return newPoints;
    }

    public static void checkFill(GamePlayer gamePlayer, BattleBallTile tile, Collection<BattleBallTile> updateFillTiles) {
         GameTeam team = gamePlayer.getTeam();

        for (BattleBallTile neighbour : FloodFill.neighbours(gamePlayer.getGame(), tile.getPosition())) {
            if (neighbour == null || neighbour.getState() == BattleBallTileState.SEALED || neighbour.getColour() == BattleBallColourState.DISABLED) {
                continue;
            }

            var fillTiles = FloodFill.getFill(gamePlayer, neighbour);

            if (fillTiles.size() > 0) {
                for (BattleBallTile filledTile : FloodFill.getFill(gamePlayer, neighbour)) {
                    if (filledTile.getState() == BattleBallTileState.SEALED) {
                        continue;
                    }

                    team.setSealedTileScore();

                    filledTile.setColour(tile.getColour());
                    filledTile.setState(BattleBallTileState.SEALED);

                    updateFillTiles.add(filledTile);
                }
            }
        }
    }

    /**
     * Get the current colour of this tile
     *
     * @return the colour
     */
    public BattleBallColourState getColour() {
        return colour;
    }

    /**
     * Set the current colour of this tile
     *
     * @param colour the current colour
     */
    public void setColour(BattleBallColourState colour) {
        this.colour = colour;
    }

    /**
     * Get the current state of this tile
     *
     * @return the state
     */
    public BattleBallTileState getState() {
        return state;
    }

    /**
     * Set the current state of this tile
     *
     * @param state the current colour
     */
    public void setState(BattleBallTileState state) {
        this.state = state;
    }
}
