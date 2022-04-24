package org.alexdev.kepler.game.games.battleball;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.GameEvent;
import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.GameTile;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallColourState;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallTileState;
import org.alexdev.kepler.game.games.battleball.powerups.NailBoxHandle;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;
import org.alexdev.kepler.game.games.utils.FloodFill;
import org.alexdev.kepler.game.games.utils.ScoreReference;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.mapping.RoomTile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BattleBallTile extends GameTile  {
    private BattleBallColourState colour;
    private BattleBallTileState state;

    private CopyOnWriteArrayList<ScoreReference> pointsReferece;

    public BattleBallTile(Position position) {
        super(position);
        this.pointsReferece = new CopyOnWriteArrayList<>();
    }

    public List<GamePlayer> getPlayers(Game game, Position position) {
        RoomTile tile = game.getRoom().getMapping().getTile(position.getX(), position.getY());
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

            if (!player.getRoomUser().getPosition().equals(this.getPosition())) {
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
            BattleBallTileState newState = this.getState();
            BattleBallColourState newColour = this.getColour();

            if (colour.getColourId() == team.getId()) {
                newState = BattleBallTileState.getStateById(state.getTileStateId() + 1);
            } else {
                if (gamePlayer.getGame().getMapId() == 5) { // Barebones classic takes 4 hits
                    newState = BattleBallTileState.TOUCHED;
                    newColour = BattleBallColourState.getColourById(team.getId());
                } else {
                    newState = BattleBallTileState.CLICKED;
                    newColour = BattleBallColourState.getColourById(team.getId());
                }
            }

            this.getNewPoints(gamePlayer, newState, newColour);

            this.setColour(newColour);
            this.setState(newState);

            if (newState == BattleBallTileState.SEALED) {
                this.checkFill(gamePlayer, updateFillTiles);
            }

            updateTiles.add(this);
        }
    }

    public void getNewPoints(GamePlayer gamePlayer, BattleBallTileState newState, BattleBallColourState newColour) {
        GameTeam team = gamePlayer.getTeam();

        int newPoints = -1;
        //boolean sealed = false;

        if (newState == BattleBallTileState.TOUCHED) {
            newPoints = 2;
        } else if (newState == BattleBallTileState.CLICKED) {
            newPoints = 6;
        } else if (newState == BattleBallTileState.PRESSED) {
            newPoints = 10;
        } else if (newState == BattleBallTileState.SEALED) {
            if (this.colour == newColour) {
                newPoints = 14;
                //sealed = true;
            }
        }

        // If the user stole the tile, add the points!
        if (this.colour != newColour && this.colour != BattleBallColourState.DEFAULT) {
            newPoints += 2;
        }

        if (this.colour != newColour) { // Clear teams previous scores
            this.pointsReferece.removeIf(tile -> tile.getGameTeam() != team);
        }

        if (newPoints != -1) {
            //if (!sealed) { // Set to sealed
            if (gamePlayer.getHarlequinPlayer() != null) {
                this.pointsReferece.add(new ScoreReference(newPoints, team, gamePlayer.getHarlequinPlayer().getUserId()));
            } else {
                this.pointsReferece.add(new ScoreReference(newPoints, team, gamePlayer.getUserId()));
            }
            //} else {
            //    this.pointsReferece.add(team);
            //}
        }
    }


    public void checkFill(GamePlayer gamePlayer, Collection<BattleBallTile> updateFillTiles) {
        GameTeam team = gamePlayer.getTeam();

        for (BattleBallTile neighbour : FloodFill.neighbours(gamePlayer.getGame(), this.getPosition())) {
            if (neighbour == null || neighbour.getState() == BattleBallTileState.SEALED || neighbour.getColour() == BattleBallColourState.DISABLED) {
                continue;
            }

            var fillTiles = FloodFill.getFill(gamePlayer, neighbour);

            if (fillTiles.size() > 0) {
                for (BattleBallTile filledTile : FloodFill.getFill(gamePlayer, neighbour)) {
                    if (filledTile.getState() == BattleBallTileState.SEALED) {
                        continue;
                    }

                    /*if (!filledTile.isCanFill()) {
                        continue;
                    }*/

                    //this.addSealedPoints(team);

                    filledTile.setColour(this.getColour());
                    filledTile.setState(BattleBallTileState.SEALED);

                    updateFillTiles.add(filledTile);
                }
            }
        }
    }

    /*
    public void addSealedPoints(GameTeam team) {
        this.pointsReferece.clear();

        for (GamePlayer gamePlayer : team.getPlayers()) {
            this.pointsReferece.add(new ScoreReference(14, team, gamePlayer.getUserId()));
        }
    }*/

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

    /**
     * Get the list of points to reference.
     *
     * @return the list of points to reference
     */
    public CopyOnWriteArrayList<ScoreReference> getPointsReferece() {
        return pointsReferece;
    }
}
