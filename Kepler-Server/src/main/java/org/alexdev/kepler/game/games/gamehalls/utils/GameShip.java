package org.alexdev.kepler.game.games.gamehalls.utils;

import org.alexdev.kepler.game.games.gamehalls.GameBattleShip;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;

public class GameShip {
    private final GameBattleShip game;
    private final GameShipType shipType;
    private final Position position;
    private final Player player;
    private final boolean isHorizontal;

    public GameShip(GameBattleShip game, GameShipType shipType, Position position, Player player, boolean isHorizontal) {
        this.game = game;
        this.shipType = shipType;
        this.position = position;
        this.player = player;
        this.isHorizontal = isHorizontal;
    }


    public final GameShipType getShipType() {
        return shipType;
    }

    public final Position getPosition() {
        return position;
    }

    public final Player getPlayer() {
        return player;
    }

    public int getHits() {
        int hits = 0;

        for (int i = 0; i < this.shipType.getLength(); i++) {
            int shipX = this.position.getX() + (isHorizontal ? i : 0);
            int shipY = this.position.getY() + (isHorizontal ? 0 : i);

            GameShipMove shipMove = this.game.getPlayerListMap().get(this.game.getOppositePlayer(this.player)).stream()
                    .filter(move ->
                            move.getX() == shipX &&
                                    move.getY() == shipY)
                    .findFirst().orElse(null);

            if (shipMove == null) {
                continue;
            }

            if (shipMove.getMoveResult() == GameShipMoveResult.HIT) {
                hits++;
            }
        }

        return hits;
    }

    public boolean isHitTwice() {
        int hits = this.getHits();
        return hits >= 2 && hits != this.shipType.getLength();
    }

    public boolean isDestroyed() {
        return this.getHits() == this.shipType.getLength();
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }
}
