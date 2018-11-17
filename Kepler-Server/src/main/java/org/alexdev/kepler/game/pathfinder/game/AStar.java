package org.alexdev.kepler.game.pathfinder.game;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.pathfinder.Pathfinder;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.mapping.RoomTileState;
import org.alexdev.kepler.game.room.models.RoomModel;

import java.security.PublicKey;
import java.util.*;

import static org.alexdev.kepler.game.pathfinder.Pathfinder.DIAGONAL_MOVE_POINTS;

public class AStar {
    private AreaNode[][] nodes;

    private final RoomModel roomModel;
	private final Comparator<AreaNode> fComparator = Comparator.comparingInt(AreaNode::getFValue);

	public AStar(RoomModel roomModel) {
		this.roomModel = roomModel;
		this.nodes = new AreaNode[roomModel.getMapSizeX()][roomModel.getMapSizeY()];

        for (int y = 0; y < this.roomModel.getMapSizeY(); y++) {
            for (int x = 0; x < this.roomModel.getMapSizeX(); x++) {
                this.nodes[x][y] = new AreaNode(new Position(x, y));
            }
        }
	}

	private AreaNode getNode(Position position) {
        if (position.getX() < 0 || position.getY() < 0) {
            return null;
        }

        if (position.getX() >= this.roomModel.getMapSizeX() || position.getY() >= this.roomModel.getMapSizeY()) {
            return null;
        }

        return this.nodes[position.getX()][position.getY()];
    }

	public LinkedList<Position> calculateAStarNoTerrain(Entity entity, Position p1, Position p2) {
		List<AreaNode> openList = new LinkedList<>();
		Set<AreaNode> closedList = new HashSet<>();

		AreaNode destNode = this.getNode(p2);
		AreaNode currentNode = this.getNode(p1);

		currentNode.parent = null;
		currentNode.setGValue(0);
		openList.add(currentNode);

		while (!openList.isEmpty()) {
			openList.sort(this.fComparator);
			currentNode = openList.get(0);

			if (currentNode.point.equals(destNode.point)) {
				return this.calculatePath(destNode);
			}

			openList.remove(currentNode);
			closedList.add(currentNode);

			for (Position point : DIAGONAL_MOVE_POINTS) {
				Position adjPoint = currentNode.point.copy().add(point);
                AreaNode adjNode = this.getNode(adjPoint);

                if (adjNode == null) {
                    continue;
                }

				boolean isFinalMove = adjPoint.equals(destNode.point);

				if (Pathfinder.isValidStep(entity.getRoomUser().getRoom(), entity, currentNode.point.copy(), adjPoint.copy(), isFinalMove)) {
					if (!closedList.contains(adjNode)) {
						if (!openList.contains(adjNode)) {
							adjNode.parent = currentNode;
							adjNode.calculateGValue(currentNode);
							adjNode.calculateHValue(destNode);
							openList.add(adjNode);
						} else {
							if (adjNode.gValue < currentNode.gValue) {
								adjNode.calculateGValue(currentNode);
								currentNode = adjNode;
							}
						}
					}
				}
			}
		}

		return null;
	}

	private LinkedList<Position> calculatePath(AreaNode destinationNode) {
		LinkedList<Position> path = new LinkedList<Position>();

		if (destinationNode.parent == null) {
		    //path.add(destinationNode.point);
		    return path;
        }

		AreaNode node = destinationNode;
		while (node.parent != null) {
			path.add(node.point);
			node = node.parent;
		}

        Collections.reverse(path);

		return path;
	}
}