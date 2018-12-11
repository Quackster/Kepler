package org.alexdev.roseau.game.pathfinder;

import java.util.LinkedList;

import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.room.model.Position;

import com.google.common.collect.Lists;
import com.google.common.collect.MinMaxPriorityQueue;

public class Pathfinder {

	public static final Position[] MOVE_POINTS = new Position[]{
			new Position(0, -1, 0),
			new Position(0, 1, 0),
			new Position(1, 0, 0),
			new Position(-1, 0, 0),
			new Position(1, -1, 0),
			new Position(-1, 1, 0),
			new Position(1, 1, 0),
			new Position(-1, -1, 0)
	};
	
	public static LinkedList<Position> makePath(Entity entity) {

		LinkedList<Position> squares = new LinkedList<>();

		PathfinderNode nodes = makePathReversed(entity);

		if (nodes != null) {
			while (nodes.getNextNode() != null) {
				squares.add(new Position(nodes.getPosition().getX(), nodes.getPosition().getY()));
				nodes = nodes.getNextNode();
			}
		}

		return new LinkedList<Position>(Lists.reverse(squares));
	}

	private static PathfinderNode makePathReversed(Entity entity) {
		MinMaxPriorityQueue<PathfinderNode> openList = MinMaxPriorityQueue.maximumSize(256).create();

		PathfinderNode[][] map = new PathfinderNode[entity.getRoomUser().getModel().getMapSizeX()][entity.getRoomUser().getModel().getMapSizeY()];
		PathfinderNode node;
		Position tmp;

		int cost;
		int diff;

		PathfinderNode current = new PathfinderNode(entity.getRoomUser().getPosition());
		current.setCost(0);

		Position end = entity.getRoomUser().getGoal();
		PathfinderNode finish = new PathfinderNode(end);

		map[current.getPosition().getX()][current.getPosition().getY()] = current;
		openList.add(current);

		while (openList.size() > 0) {
			current = openList.pollFirst();
			current.setInClosed(true);

			for (int i = 0; i < MOVE_POINTS.length; i++) {
				tmp = current.getPosition().add(MOVE_POINTS[i]);

				boolean isFinalMove = (tmp.getX() == end.getX() && tmp.getY() == end.getY());

				if (entity.getRoomUser().getRoom().isValidStep(entity, new Position(current.getPosition().getX(), current.getPosition().getY(), current.getPosition().getZ()), tmp, isFinalMove)) {
					if (map[tmp.getX()][tmp.getY()] == null) {
						node = new PathfinderNode(tmp);
						map[tmp.getX()][tmp.getY()] = node;
					} else {
						node = map[tmp.getX()][tmp.getY()];
					}

					if (!node.isInClosed()) {
						diff = 0;

						if (current.getPosition().getX() != node.getPosition().getX()) {
							diff += 1;
						}

						if (current.getPosition().getY() != node.getPosition().getY()) {
							diff += 1;
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