package org.alexdev.roseau.game.pathfinder;

import java.util.LinkedList;

import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.game.room.model.Point;

import com.google.common.collect.Lists;
import com.google.common.collect.MinMaxPriorityQueue;

public class Pathfinder {

	public static final Point[] MOVE_POINTS = new Point[]{
			new Point(0, -1, 0),
			new Point(0, 1, 0),
			new Point(1, 0, 0),
			new Point(-1, 0, 0),
			new Point(1, -1, 0),
			new Point(-1, 1, 0),
			new Point(1, 1, 0),
			new Point(-1, -1, 0)
	};
	
	public static LinkedList<Point> makePath(IEntity entity) {

		LinkedList<Point> squares = new LinkedList<>();

		PathfinderNode nodes = makePathReversed(entity);

		if (nodes != null) {
			while (nodes.getNextNode() != null) {
				squares.add(new Point(nodes.getPosition().getX(), nodes.getPosition().getY()));
				nodes = nodes.getNextNode();
			}
		}

		return new LinkedList<Point>(Lists.reverse(squares));
	}

	private static PathfinderNode makePathReversed(IEntity entity) {
		MinMaxPriorityQueue<PathfinderNode> openList = MinMaxPriorityQueue.maximumSize(256).create();

		PathfinderNode[][] map = new PathfinderNode[entity.getRoomEntity().getModel().getMapSizeX()][entity.getRoomEntity().getModel().getMapSizeY()];
		PathfinderNode node;
		Point tmp;

		int cost;
		int diff;

		PathfinderNode current = new PathfinderNode(entity.getRoomEntity().getPosition());
		current.setCost(0);

		Point end = entity.getRoomEntity().getGoal();
		PathfinderNode finish = new PathfinderNode(end);

		map[current.getPosition().getX()][current.getPosition().getY()] = current;
		openList.add(current);

		System.out.println("openList: " + (openList.size() > 0));

		while (openList.size() > 0) {
			current = openList.pollFirst();
			current.setInClosed(true);

			for (int i = 0; i < MOVE_POINTS.length; i++) {
				tmp = current.getPosition().add(MOVE_POINTS[i]);

				boolean isFinalMove = (tmp.getX() == end.getX() && tmp.getY() == end.getY());

				if (entity.getRoomEntity().getRoom().isValidStep(new Point(current.getPosition().getX(), current.getPosition().getY(), current.getPosition().getZ()), tmp, isFinalMove)) {
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