package org.alexdev.kepler.game.pathfinder;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.mapping.RoomTile;

import java.util.LinkedList;

public class Pathfinder {
    public static final double MAX_DROP_HEIGHT = 3.0;
    public static final double MAX_LIFT_HEIGHT = 1.5;

    public static final Position[] DIAGONAL_MOVE_POINTS = new Position[]{
            new Position(0, -1, 0),
            new Position(0, 1, 0),
            new Position(1, 0, 0),
            new Position(-1, 0, 0),
            new Position(1, -1, 0),
            new Position(-1, 1, 0),
            new Position(1, 1, 0),
            new Position(-1, -1, 0)
    };

    public static final Position[] DIAGONAL_MOVE_POINTS2 = new Position[]{
            new Position(1, 0, 0),//
            new Position(1, 1, 0),//
            new Position(0, 1, 0),//
            new Position(-1, 1, 0),//
            new Position(-1, 0, 0),//
            new Position(-1, -1, 0),//
            new Position(0, -1, 0),
            new Position(1, -1, 0)
    };

    public static final Position[] MOVE_POINTS = new Position[]{
            new Position(0, -1),
            new Position(1, 0),
            new Position(0, 1),
            new Position(-1, 0)
    };

    /**
     * Method for the pathfinder to check if the tile next to the current tile is a valid step.
     *
     * @param entity the entity walking
     * @param current the current tile
     * @param tmp the temporary tile around the current tile to check
     * @param isFinalMove if the move was final
     * @return true, if a valid step
     */
    public static boolean isValidStep(Room room, Entity entity, Position current, Position tmp, boolean isFinalMove) {
        if (entity.getRoomUser().getRoom() == null || entity.getRoomUser().getRoom().getModel() == null) {
            return false;
        }

        if (!RoomTile.isValidTile(room, entity, new Position(current.getX(), current.getY()))) {
            return false;
        }

        if (!RoomTile.isValidTile(room, entity, new Position(tmp.getX(), tmp.getY()))) {
            return false;
        }

        RoomTile fromTile = room.getMapping().getTile(current);
        RoomTile toTile = room.getMapping().getTile(tmp);

        if (fromTile == null || toTile == null) {
            return false;
        }

        double oldHeight = fromTile.getWalkingHeight();
        double newHeight = toTile.getWalkingHeight();

        Item fromItem = fromTile.getHighestItem();
        Item toItem = toTile.getHighestItem();

        // boolean hasPool = room.getModel().getName().startsWith("pool_") || room.getModel().getName().equals("md_a");
        // boolean isPrivateRoom =  !room.isPublicRoom();

        boolean fromItemHeightExempt = fromItem != null && (fromItem.hasBehaviour(ItemBehaviour.TELEPORTER)
                || (fromItem.getDefinition().getSprite().equals("wsJoinQueue"))
                || (fromItem.getDefinition().getSprite().equals("wsQueueTile"))
                || (fromItem.getDefinition().getSprite().equals("poolEnter"))
                || (fromItem.getDefinition().getSprite().equals("poolExit")));

        boolean toItemHeightExempt = toItem != null && (toItem.hasBehaviour(ItemBehaviour.TELEPORTER)
                || (toItem.getDefinition().getSprite().equals("wsJoinQueue"))
                || (toItem.getDefinition().getSprite().equals("wsQueueTile"))
                || (toItem.getDefinition().getSprite().equals("poolEnter"))
                || (toItem.getDefinition().getSprite().equals("poolExit")));

        if (fromItem == null || !fromItemHeightExempt) {
            if (fromTile.isHeightUpwards(toTile)) {
                if ((oldHeight - newHeight) > MAX_LIFT_HEIGHT) {
                    return false;
                }
            }

            if (fromTile.isHeightDrop(toTile)) {
                if ((newHeight - oldHeight) > MAX_DROP_HEIGHT) {
                    return false;
                }
            }
        }

        if (toItem == null || !toItemHeightExempt) {
            if (toTile.isHeightUpwards(fromTile)) {
                if ((oldHeight - newHeight) > MAX_LIFT_HEIGHT) {
                    return false;
                }
            }

            if (toTile.isHeightDrop(fromTile)) {
                if ((newHeight - oldHeight) > MAX_DROP_HEIGHT) {
                    return false;
                }
            }
        }

        /*if (hasPool || isPrivateRoom) {
            if (hasPool) {
                if (oldHeight - 3 >= newHeight) {
                    return fromItem != null && (fromItem.hasBehaviour(ItemBehaviour.TELEPORTER)
                            || (fromItem.getDefinition().getSprite().equals("wsJoinQueue"))
                            || (fromItem.getDefinition().getSprite().equals("wsQueueTile"))
                            || (fromItem.getDefinition().getSprite().equals("poolEnter"))
                            || (fromItem.getDefinition().getSprite().equals("poolExit")));
                }

            }

            if (oldHeight + 1.5 <= newHeight) {
                return toItem != null && (toItem.hasBehaviour(ItemBehaviour.TELEPORTER)
                        || (toItem.getDefinition().getSprite().equals("wsJoinQueue"))
                        || (toItem.getDefinition().getSprite().equals("poolEnter"))
                        || (toItem.getDefinition().getSprite().equals("poolExit")));
            }
        } else {
            // Apply this to the rest of public rooms
            if (oldHeight - 1.5 >= newHeight) {
                return false;
            }

            if (oldHeight + 1.5 <= newHeight) {
                return false;
            }
        }*/

        // Only check these below if the user is in a pool room.
        if (room.getModel().getName().startsWith("pool_") ||
                room.getModel().getName().equals("md_a")) {
            if (toItem != null) {
                // Check if they have swimmers before trying to enter pool
                if (toItem.getDefinition().getSprite().equals("poolEnter") ||
                        toItem.getDefinition().getSprite().equals("poolExit")) {
                    return entity.getDetails().getPoolFigure().length() > 0;
                }

                // Don't allow to "enter" the pool if they're already swimming
                if (entity.getRoomUser().containsStatus(StatusType.SWIM) &&
                        toItem.getDefinition().getSprite().equals("poolEnter")) {
                    return false;
                }

                // Don't allow to "leave" the pool if they're not swimming
                if (!entity.getRoomUser().containsStatus(StatusType.SWIM) &&
                        toItem.getDefinition().getSprite().equals("poolExit")) {
                    return false;
                }

                // Don't allow users to cut people in queue, force them to garound
                if (toItem.getDefinition().getSprite().contains("queue_tile2")) {
                    RoomTile tile = room.getMapping().getTile(entity.getRoomUser().getGoal());

                    if (tile == null || tile.getHighestItem() == null || !tile.getHighestItem().getDefinition().getSprite().contains("queue_tile2")) {
                        return false;
                    }
                }

                // Don't allow people to enter the booth if it's closed, or don't allow
                // if they attempt to use the pool lift without swimmers
                if (toItem.getDefinition().getSprite().equals("poolBooth") ||
                        toItem.getDefinition().getSprite().equals("poolLift")) {

                    if (toItem.getCurrentProgramValue().equals("close")) {
                        return false;
                    } else {
                        return !toItem.getDefinition().getSprite().equals("poolLift") || entity.getDetails().getPoolFigure().length() > 0;
                    }
                }
            }
        }

        // Can't walk diagonal between two non-walkable tiles.
        if (!room.getModel().getName().startsWith("sun_terrace")) { // Don't enable diagonal checking for the Sun Terrace
            if (current.getX() != tmp.getX() && current.getY() != tmp.getY()) {

                boolean firstValidTile = RoomTile.isValidDiagonalTile(room, entity, new Position(tmp.getX(), current.getY()));
                boolean secondValidTile = RoomTile.isValidDiagonalTile(room, entity, new Position(current.getX(), tmp.getY()));

                if (!firstValidTile && !secondValidTile) {
                    return false;
                }
            }
        }

        // Avoid walking into furniture unless it's their last location
        if (!current.equals(room.getModel().getDoorLocation())) {
            if (toItem != null) {
                if (isFinalMove) {
                    return toItem.isWalkable();
                } else {
                    return toItem.hasBehaviour(ItemBehaviour.CAN_STAND_ON_TOP) || toItem.isGateOpen();
                }
            }
        }

        return true;
    }

    /**
     * Make path with specified last coordinates
     *
     * @param entity the entity to move
     * @return the linked list
     */
    public static LinkedList<Position> makePath(Entity entity, Position start, Position end) {//List<PathfinderSettings> settings) {
        LinkedList<Position> squares = new LinkedList<>();
        PathfinderNode nodes = makePathReversed(entity, end, start);

        if (nodes != null) {
            while (nodes.getNextNode() != null) {
                squares.add(nodes.getNextNode().getPosition());
                nodes = nodes.getNextNode();
            }
        }

        //Collections.reverse(squares);
        return squares;

    }

    /**
     * Make path reversed.
     *
     * @param entity the entity
     * @return the pathfinder node
     */
    private static PathfinderNode makePathReversed(Entity entity, Position start, Position end) {
        LinkedList<PathfinderNode> openList = new LinkedList<>();

        PathfinderNode[][] map = new PathfinderNode[entity.getRoomUser().getRoom().getModel().getMapSizeX()][entity.getRoomUser().getRoom().getModel().getMapSizeY()];
        PathfinderNode node = null;
        Position tmp;

        int cost;
        int diff;

        PathfinderNode current = new PathfinderNode(start);
        current.setCost(0);

        PathfinderNode finish = new PathfinderNode(end);

        map[current.getPosition().getX()][current.getPosition().getY()] = current;
        openList.add(current);

        while (openList.size() > 0) {
            current = openList.pollFirst();
            current.setInClosed(true);

            for (Position POINT : DIAGONAL_MOVE_POINTS) {
                tmp = current.getPosition().add(POINT);

                boolean isFinalMove = (tmp.getX() == end.getX() && tmp.getY() == end.getY());

                if (isValidStep(entity.getRoomUser().getRoom(), entity, new Position(current.getPosition().getX(), current.getPosition().getY(), current.getPosition().getZ()), tmp, isFinalMove)) {
                    if (map[tmp.getX()][tmp.getY()] == null) {
                        node = new PathfinderNode(tmp);
                        map[tmp.getX()][tmp.getY()] = node;
                    } else {
                        node = map[tmp.getX()][tmp.getY()];
                    }

                    if (!node.isInClosed()) {
                        diff = 0;

                        if (current.getPosition().getX() != node.getPosition().getX()) {
                            diff += 2; // Reminder: It was 1 up until 29/08/2018
                        }

                        if (current.getPosition().getY() != node.getPosition().getY()) {
                            diff += 2; // Reminder: It was 1 up until 29/08/2018
                        }

                        cost = current.getCost() + diff + node.getPosition().getDistanceSquared(end);

                        if (cost < node.getCost()) {
                            node.setCost(cost);
                            node.setNextNode(current);
                        }

                        if (!node.isInOpen()) {
                            if (node.getPosition().getX() == finish.getPosition().getX() && node.getPosition().getY() == finish.getPosition().getY()) {
                                node.setNextNode(current);
                                return node;
                            }

                            node.setInOpen(true);
                            openList.add(node);
                        }
                    }
                }
            }
        }

        return null;
    }
}