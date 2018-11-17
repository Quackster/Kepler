package org.alexdev.kepler.game.item.roller;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.game.room.models.RoomModel;
import org.alexdev.kepler.messages.outgoing.rooms.items.SLIDEOBJECTBUNDLE;
import org.alexdev.kepler.util.config.GameConfiguration;

public class ItemRollingAnalysis implements RollingAnalysis<Item> {
    @Override
    public Position canRoll(Item item, Item roller, Room room) {
        if (roller == null) {
            return null;
        }

        if (item.getId() == roller.getId()) {
            return null;
        }

        if (item.getPosition().getZ() < roller.getPosition().getZ()) {
            return null;
        }

        Position front = roller.getPosition().getSquareInFront();
        RoomTile frontTile = room.getMapping().getTile(front);

        if (frontTile == null) {
            return null;
        }

        if (!frontTile.hasWalkableFurni()) {
            return null;
        }

        // Check all entities in the room
        for (Entity e : room.getEntities()) {
            if (e.getRoomUser().getRoom() == null) {
                continue;
            }

            // Don't roll if an entity is going to walk into the furniture
            if (e.getRoomUser().getNextPosition() != null) {
                if (e.getRoomUser().getNextPosition().equals(front)) {
                    return null;
                }
            }

            // Ignore people who are walking
            if (e.getRoomUser().isWalking()) {
                continue;
            }

            // Don't roll if there's an entity rolling into you
            if (e.getRoomUser().getRollingData() != null) {
                if (e.getRoomUser().getRollingData().getNextPosition().equals(front)) {
                    return null;
                }
            }

            if (e.getRoomUser().getPosition().equals(front)) {
                return null;
            }
        }

        // Check all rolling items in the room
        for (Item floorItem : room.getItemManager().getFloorItems()) {
            if (floorItem.getRollingData() != null) {
                if (floorItem.getPosition().equals(roller.getPosition())) {
                    continue;
                }

                // Don't roll if there's another item that's going to roll into this item
                if (floorItem.getRollingData().getNextPosition().equals(front)) {
                    return null;
                }
            }
        }

        double nextHeight = item.getPosition().getZ();//this.room.getModel().getTileHeight(roller.getPosition().getX(), roller.getPosition().getY());
        boolean subtractRollerHeight = true;

        if (frontTile.getHighestItem() != null) {
            Item frontRoller = null;

            for (Item frontItem : frontTile.getItems()) {
                if (!frontItem.hasBehaviour(ItemBehaviour.ROLLER)) {
                    continue;
                }

                frontRoller = frontItem;
            }

            if (frontRoller != null) {
                subtractRollerHeight = false;

                if (frontRoller.getPosition().getZ() != roller.getPosition().getZ()) {
                    if (Math.abs(frontRoller.getPosition().getZ() - roller.getPosition().getZ()) > 0.1) {
                        return null; // Don't roll if the height of the roller is different by >0.1
                    }
                }

                for (Item frontItem : frontTile.getItems()) {
                    if (frontItem.getPosition().getZ() < frontRoller.getPosition().getZ()) {
                        continue;
                    }

                    if (frontItem.getId() == item.getId()) {
                        continue;
                    }

                    if (frontItem.hasBehaviour(ItemBehaviour.ROLLER)) {
                        Position frontPosition = frontRoller.getPosition().getSquareInFront();

                        // Don't roll an item into the next roller, if the next roller is facing towards the roller
                        // it just rolled from, and the next roller has an item on it.
                        if (frontPosition.equals(item.getPosition())) {
                            if (frontTile.getItems().size() > 1 || frontTile.getEntities().size() > 0) {
                                return null;

                            }
                        }
                    } else {
                        return null;/* else {
                        if (frontItem.hasBehaviour(ItemBehaviour.CAN_STACK_ON_TOP)) {
                            if (item.hasBehaviour(ItemBehaviour.CAN_STACK_ON_TOP)) {
                                return null;
                            }

                            frontItem.setStopRoll(true);
                            nextHeight += frontItem.getDefinition().getTopHeight();
                        } else {
                            return null;
                        }
                    }*/
                    }
                }
            } else {
                if (!RoomTile.isValidTile(room, null, frontTile.getPosition())) {
                    return null;
                }
            }
        }

        if (subtractRollerHeight) {
            nextHeight -= roller.getDefinition().getTopHeight();
        }

        if (nextHeight > GameConfiguration.getInstance().getInteger("stack.height.limit")) {
            nextHeight = GameConfiguration.getInstance().getInteger("stack.height.limit");
        }

        Position nextPosition = new Position(front.getX(), front.getY(), nextHeight);
        item.setRollingData(new RollingData(item, roller, item.getPosition().copy(), nextPosition));
        return nextPosition;
    }
    
    @Override
    public void doRoll(Item item, Item roller, Room room, Position fromPosition, Position nextPosition) {
        /*RoomTile roomTile;

        roomTile = room.getMapping().getTile(nextPosition);
        roomTile.setDisableWalking(true);

        roomTile = room.getMapping().getTile(fromPosition);
        roomTile.setDisableWalking(true);*/

        //room.send(new SLIDE_OBJECT(item, nextPosition, roller.getId(), nextPosition.getZ()));

        item.getPosition().setX(nextPosition.getX());
        item.getPosition().setY(nextPosition.getY());
        item.getPosition().setZ(nextPosition.getZ());
        //item.setRollingData(new RollingData(item, roller, fromPosition, nextPosition));
    }
}
