package org.alexdev.kepler.game.games.battleball.powerups;

import org.alexdev.kepler.game.games.battleball.BattleBallGame;
import org.alexdev.kepler.game.games.battleball.BattleBallTile;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallPlayerState;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.utils.PowerUpUtil;
import org.alexdev.kepler.game.games.utils.TileUtil;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pathfinder.Rotation;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.mapping.RoomTile;

import java.util.ArrayList;
import java.util.List;

public class BombHandle {
    public static void handle(BattleBallGame game, GamePlayer gamePlayer, Room room) {
        //PowerUpUtil.stunPlayer(game, gamePlayer, BattleBallPlayerState.STUNNED);
        List<GamePlayer> stunnedPlayers = new ArrayList<>();

        for (Position position : gamePlayer.getPlayer().getRoomUser().getPosition().getCircle(5)) {
            RoomTile tile = game.getRoom().getMapping().getTile(position.getX(), position.getY());

            if (tile == null || !RoomTile.isValidTile(gamePlayer.getGame().getRoom(), null, position)) {
                continue;
            }

            BattleBallTile battleballTile = (BattleBallTile) game.getTile(position.getX(), position.getY());

            if (TileUtil.undoTileAttributes(battleballTile, gamePlayer.getGame())) {
                game.getUpdateTilesQueue().add(battleballTile);
            }

            stunnedPlayers.addAll(battleballTile.getPlayers(gamePlayer));
        }

        for (GamePlayer stunnedPlayer : stunnedPlayers) {
            // Move player away from blast radius: https://www.youtube.com/watch?v=cP3bvGOx53o&feature=youtu.be&t=242
            if (gamePlayer != stunnedPlayer) {
                Position from = stunnedPlayer.getPlayer().getRoomUser().getPosition();
                Position towards = gamePlayer.getPlayer().getRoomUser().getPosition();

                int temporaryRotation = Rotation.calculateWalkDirection(from, towards);

                Position pushBack = from.copy();
                pushBack.setRotation(temporaryRotation);
                pushBack = pushBack.getSquareBehind();

                BattleBallTile battleballTile = (BattleBallTile) game.getTile(pushBack.getX(), pushBack.getY());

                if (TileUtil.isValidGameTile(stunnedPlayer, battleballTile, true)) {
                    stunnedPlayer.getPlayer().getRoomUser().getRoom().getMapping().getTile(from).removeEntity(stunnedPlayer.getPlayer());
                    stunnedPlayer.getPlayer().getRoomUser().getRoom().getMapping().getTile(pushBack).addEntity(stunnedPlayer.getPlayer());

                    stunnedPlayer.getPlayer().getRoomUser().getPosition().setX(pushBack.getX());
                    stunnedPlayer.getPlayer().getRoomUser().getPosition().setY(pushBack.getY());
                    stunnedPlayer.getPlayer().getRoomUser().getPosition().setZ(battleballTile.getPosition().getZ());
                }
            }

            PowerUpUtil.stunPlayer(game, stunnedPlayer, BattleBallPlayerState.STUNNED);
        }
    }
}
