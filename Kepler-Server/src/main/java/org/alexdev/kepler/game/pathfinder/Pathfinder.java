package org.alexdev.kepler.game.pathfinder;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.item.interactors.types.PoolInteractor;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.mapping.RoomTile;

import java.util.LinkedList;

public class Pathfinder {
    public static final double MAX_DROP_HEIGHT = 10.0;
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
     * Method for the pathfinder to check if the tile next to the newTile tile is a valid step.
     *
     * @param entity the entity walking
     * @param newTile the newTile tile
     * @param oldTile the temporary tile around the newTile tile to check
     * @param isFinalMove if the move was final
     * @return true, if a valid step
     */
    public static boolean isValidStep(Room room, Entity entity, Position newTile, Position oldTile, boolean isFinalMove) {
        if (entity.getRoomUser().getRoom() == null || entity.getRoomUser().getRoom().getModel() == null) {
            return false;
        }

        if (!RoomTile.isValidTile(room, entity, new Position(newTile.getX(), newTile.getY()))) {
            return false;
        }

        if (!RoomTile.isValidTile(room, entity, new Position(oldTile.getX(), oldTile.getY()))) {
            return false;
        }

        RoomTile toTile = room.getMapping().getTile(newTile);
        RoomTile currentTile = room.getMapping().getTile(oldTile);

        if (toTile == null || currentTile == null) {
            return false;
        }

        double oldHeight = toTile.getWalkingHeight();
        double newHeight = currentTile.getWalkingHeight();

        Item toItem = toTile.getHighestItem();
        Item fromItem = currentTile.getHighestItem();

        // boolean hasPool = room.getModel().getName().startsWith("pool_") || room.getModel().getName().equals("md_a");
        // boolean isPrivateRoom =  !room.isPublicRoom();

        boolean fromItemHeightExempt = toItem != null && (toItem.hasBehaviour(ItemBehaviour.TELEPORTER)
                || toItem.getDefinition().getSprite().equals("wsJoinQueue")
                || toItem.getDefinition().getSprite().equals("wsQueueTile")
                || (toItem.getDefinition().getSprite().equals("poolEnter") && fromItem != null && fromItem.getDefinition().getSprite().equals("poolExit")) // No height check when going between pool triggers
                || (toItem.getDefinition().getSprite().equals("poolExit") && fromItem != null && fromItem.getDefinition().getSprite().equals("poolEnter")) // No height check when going between pool triggers
                || toItem.getDefinition().getSprite().equals("poolLift")
                || toItem.getDefinition().getSprite().equals("queue_tile2") && room.getData().getModel().equals("pool_b"));

        boolean toItemHeightExempt = fromItem != null && (fromItem.hasBehaviour(ItemBehaviour.TELEPORTER)
                || fromItem.getDefinition().getSprite().equals("wsJoinQueue")
                || fromItem.getDefinition().getSprite().equals("wsQueueTile")
                || (fromItem.getDefinition().getSprite().equals("poolEnter") && toItem != null && toItem.getDefinition().getSprite().equals("poolExit")) // No height check when going between pool triggers
                || (fromItem.getDefinition().getSprite().equals("poolExit") && toItem != null && toItem.getDefinition().getSprite().equals("poolEnter")) // No height check when going between pool triggers
                || fromItem.getDefinition().getSprite().equals("poolLift")
                || fromItem.getDefinition().getSprite().equals("queue_tile2") && room.getData().getModel().equals("pool_b"));

        // Pathfinder makes the path from reversed, so we compare the drop reversed (To tile height against From tile height)
        /*if (toTile.isHeightUpwards(toTile) && (!fromItemHeightExempt && !toItemHeightExempt)) {
            if (Math.abs(newHeight - oldHeight) > MAX_LIFT_HEIGHT) {
                return false;
            }
        }*/

        if (currentTile.isHeightDrop(toTile) && (!fromItemHeightExempt && !toItemHeightExempt)) {
            if (Math.abs(oldHeight - newHeight) > MAX_DROP_HEIGHT) {
                return false;
            }
        }

        if (toTile.isHeightUpwards(currentTile) && (!fromItemHeightExempt && !toItemHeightExempt)) {
            if (Math.abs(newHeight - oldHeight) > MAX_LIFT_HEIGHT) {
                return false;
            }
        }

        if (toTile.isHeightDrop(currentTile) && (!fromItemHeightExempt && !toItemHeightExempt)) {
            if (Math.abs(oldHeight - newHeight) > MAX_DROP_HEIGHT) {
                return false;
            }
        }

        if (!PoolInteractor.getTileStatus(room, entity, newTile, oldTile, isFinalMove)) {
            return false;
        }

        // Don't enable diagonal checking for the Sun Terrace
        // Don't allow diagonal for pool triggers
        boolean canWalkDiagonal = !room.getModel().getName().startsWith("sun_terrace") &&
                !(toItem != null && toItem.getDefinition().getSprite().equals("poolExit")) &&
                !(toItem != null && toItem.getDefinition().getSprite().equals("poolEnter")) &&
                !(fromItem != null && fromItem.getDefinition().getSprite().equals("poolExit")) &&
                !(fromItem != null && fromItem.getDefinition().getSprite().equals("poolEnter"));

        // Can't walk diagonal between two non-walkable tiles
        if (canWalkDiagonal) {
            if (newTile.getX() != oldTile.getX() && newTile.getY() != oldTile.getY()) {

                boolean firstValidTile = RoomTile.isValidDiagonalTile(room, entity, new Position(oldTile.getX(), newTile.getY()));
                boolean secondValidTile = RoomTile.isValidDiagonalTile(room, entity, new Position(newTile.getX(), oldTile.getY()));

                if (!firstValidTile && !secondValidTile) {
                    return false;
                }
            }
        }

        // Avoid walking into furniture unless it's their last location
        if (!newTile.equals(room.getModel().getDoorLocation())) {
            if (fromItem != null) {
                if (isFinalMove) {
                    // Allow walking if item is walkable or trapped inside
                    return fromItem.isWalkable(entity) || AffectedTile.getAffectedTiles(fromItem).stream().anyMatch(x -> room.getMapping().getTile(x).containsEntity(entity));
                } else {
                    return fromItem.hasBehaviour(ItemBehaviour.CAN_STAND_ON_TOP) || fromItem.hasBehaviour(ItemBehaviour.CAN_STAND_ON_TOP) || fromItem.isGateOpen();
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

                if (isValidStep(entity.getRoomUser().getRoom(), entity, tmp, new Position(current.getPosition().getX(), current.getPosition().getY(), current.getPosition().getZ()), isFinalMove)) {
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