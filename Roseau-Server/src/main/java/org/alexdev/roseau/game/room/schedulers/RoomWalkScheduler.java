package org.alexdev.roseau.game.room.schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.game.room.model.Rotation;
import org.alexdev.roseau.messages.outgoing.STATUS;
import org.alexdev.roseau.util.StringUtil;

public class RoomWalkScheduler implements Runnable {

	private final Room room;

	public RoomWalkScheduler(Room room) {
		this.room = room;
	}

	@Override
	public void run() {
		try {
			if (this.room.isDisposed() || this.room.getEntities().isEmpty()) {
				return;
			}

			List<Entity> updateEntities = this.room.getEntities().stream()
				.filter(entity -> entity != null)
				.filter(entity -> entity.getRoomUser() != null)
				.peek(this::processEntity)
				.filter(entity -> {
					RoomUser roomEntity = entity.getRoomUser();
					if (roomEntity.needsUpdate()) {
						roomEntity.setNeedUpdate(false);
						return true;
					}
					return false;
				})
				.collect(Collectors.toList());

			if (!updateEntities.isEmpty()) {
				room.send(new STATUS(updateEntities));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processEntity(Entity entity) {
		RoomUser roomEntity = entity.getRoomUser();

		Position position = roomEntity.getPosition();
		Position goal = roomEntity.getGoal();

		if (roomEntity.isWalking()) {
			roomEntity.setLookResetTime(-1);

			java.util.Optional.ofNullable(entity.getRoomUser().getNext())
				.ifPresent(next -> {
					entity.getRoomUser().getPosition().setY(next.getY());
					entity.getRoomUser().getPosition().setX(next.getX());
					entity.getRoomUser().updateNewHeight(next);
				});

			if (!roomEntity.getPath().isEmpty()) {
				Position next = roomEntity.getPath().pop();

				if (!this.room.getMapping().isValidTile(entity, next.getX(), next.getY())) {
					roomEntity.getPath().clear();
					roomEntity.walkTo(goal.getX(), goal.getY());
					this.processEntity(entity);
					return;
				}

				roomEntity.removeStatus("lay");
				roomEntity.removeStatus("sit");

				int rotation = Rotation.calculateDirection(position.getX(), position.getY(), next.getX(), next.getY());
				double height = this.room.getMapping().getTile(next.getX(), next.getY()).getHeight();

				roomEntity.getPosition().setRotation(rotation);
				roomEntity.setStatus("mv", " " + next.getX() + "," + next.getY() + "," + StringUtil.format(height), true, -1);
				roomEntity.setNext(next);
			} else {
				roomEntity.stopWalking();
			}

			roomEntity.setNeedUpdate(true);
		}
	}
}
