package org.alexdev.kepler.game.room.public_rooms;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;

public class PoolHandler {

    /**
     * Setup booth coordinate registration in multiple areas of the map.
     * Used for both standing in the booth, and the curtain.
     *
     * @param room the room to setup the booth for
     * @param item the item to to set
     */
    public static void setupRedirections(Room room, Item item) {
        if (item.getDefinition().getSprite().equals("poolBooth")) {
            if (item.getPosition().getX() == 17 && item.getPosition().getY() == 11) {
                room.getMapping().getTile(18, 11).setHighestItem(item);
            }

            if (item.getPosition().getX() == 17 && item.getPosition().getY() == 9) {
                room.getMapping().getTile(18, 9).setHighestItem(item);
            }

            if (item.getPosition().getX() == 8 && item.getPosition().getY() == 1) {
                room.getMapping().getTile(8, 0).setHighestItem(item);
            }

            if (item.getPosition().getX() == 9 && item.getPosition().getY() == 1) {
                room.getMapping().getTile(9, 0).setHighestItem(item);
            }
        }
    }

    /**
     * Warps the player to a location fluidly with splashing.
     *
     * @param item the item, it's either a poolExit or poolEnter
     * @param entity the entity to warp
     * @param warp the warp location
     * @param goal the goal location to swim to
     * @param exit whether it was exiting or entering the ladder, to add or remove swimming
     */
    public static void warpSwim(Item item, Entity entity, Position warp, Position goal, boolean exit) {
        RoomEntity roomEntity = entity.getRoomUser();
        roomEntity.getTile().removeEntity(entity);

        Room room = entity.getRoomUser().getRoom();

        if (exit) {
            roomEntity.removeStatus(StatusType.SWIM);
        } else {
            roomEntity.setStatus(StatusType.SWIM, "");
        }

        roomEntity.setNextPosition(new Position(warp.getX(), warp.getY(), room.getMapping().getTile(warp).getTileHeight()));
        roomEntity.setWalking(true);
        roomEntity.walkTo(goal.getX(), goal.getY());

        item.showProgram(null);
    }

    /**
     * Called when a player exits a changing booth, it will automatically
     * make the player leave the booth.
     *
     * @param player the player to leave
     */
    public static void exitBooth(Player player) {
        Item item = player.getRoomUser().getCurrentItem();
        Room room = player.getRoomUser().getRoom();

        if (item == null || room == null) {
            return;
        }

        if (!item.getDefinition().getSprite().equals("poolBooth")) {
            return;
        }

        if (!room.getModel().getName().equals("pool_a") &&
            !room.getModel().getName().equals("md_a")) {
            return;
        }

        item.showProgram("open");
        player.getRoomUser().setWalkingAllowed(true);

        if (room.getData().getModel().equals("pool_a")) {
            if (player.getRoomUser().getPosition().getY() == 11) {
                player.getRoomUser().walkTo(19, 11);
            }

            if (player.getRoomUser().getPosition().getY() == 9) {
                player.getRoomUser().walkTo(19, 9);
            }
        }

        if (room.getData().getModel().equals("md_a")) {
            if (player.getRoomUser().getPosition().getX() == 8) {
                player.getRoomUser().walkTo(8, 2);
            }

            if (player.getRoomUser().getPosition().getX() == 9) {
                player.getRoomUser().walkTo(9, 9);
            }
        }
    }
}
