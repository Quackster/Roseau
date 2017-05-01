package org.alexdev.roseau.game.room.schedulers;

import java.util.ArrayList;
import java.util.List;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.game.room.model.Rotation;
import org.alexdev.roseau.messages.outgoing.STATUS;

public class RoomWalkScheduler implements Runnable {

	private Room room;

	public RoomWalkScheduler(Room room) {
		this.room = room;
	}


	@Override
	public void run() {

		try {
			if (this.room.isDisposed() || this.room.getEntities().size() == 0) {
				return;
			}

			List<Entity> update_entities = new ArrayList<Entity>();
			List<Entity> entities = this.room.getEntities();

			for (int i = 0; i < entities.size(); i++) {

				Entity entity = entities.get(i);

				if (entity != null) {
					if (entity.getRoomUser() != null) {

						this.processEntity(entity);

						RoomUser roomEntity = entity.getRoomUser();

						if (roomEntity.playerNeedsUpdate()) {
							update_entities.add(entity);
						}
					}
				}
			}

			if (update_entities.size() > 0) {
				room.send(new STATUS(update_entities));

				for (Entity entity : update_entities) {

					if (entity.getRoomUser().isWalking()) {
						if (entity.getRoomUser().getNext() != null) {

							Position next = entity.getRoomUser().getNext();

							entity.getRoomUser().getPosition().setZ(this.room.getData().getModel().getHeight(next.getX(), next.getY()));
							entity.getRoomUser().getPosition().setX(next.getX());
							entity.getRoomUser().getPosition().setY(next.getY());
						}
					}

					entity.getRoomUser().walkItemTrigger();

					if (entity.getRoomUser().playerNeedsUpdate()) {
						entity.getRoomUser().setNeedUpdate(false);
					}
				}
			}

		} catch (Exception e) {


		}
	}

	private void processEntity(Entity entity) {

		RoomUser roomEntity = entity.getRoomUser();

		if (roomEntity.isWalking()) {

			roomEntity.setLookResetTime(-1);

			if (roomEntity.getPath().size() > 0) {

				Position next = roomEntity.getPath().pop();

				boolean tileValid = true;

				/*Item item = this.room.getMapping().getHighestItem(next.getX(), next.getY());

				if (item != null) {
					if (item.getDefinition().getSprite().equals("poolLift")) {

						if (!(entity.getDetails().getTickets() > 0)) {
							if (entity.getType() == EntityType.PLAYER) {
								((Player)entity).send(new PH_NOTICKETS());
							}
						}
					}
				}*/

				if (!this.room.getMapping().isValidTile(entity, next.getX(), next.getY())) {
					tileValid = false;
				}

				if (!tileValid) {
					roomEntity.stopWalking();
					roomEntity.setNext(null);
					roomEntity.setNeedUpdate(true);
					return;
				}

				roomEntity.removeStatus("lay");
				roomEntity.removeStatus("sit");

				int rotation = Rotation.calculateHumanMoveDirection(roomEntity.getPosition().getX(), roomEntity.getPosition().getY(), next.getX(), next.getY());
				double height = this.room.getData().getModel().getHeight(next.getX(), next.getY());

				roomEntity.getPosition().setRotation(rotation);

				roomEntity.setStatus("mv", " " + next.getX() + "," + next.getY() + "," + (int)height, true, -1);
				roomEntity.setNeedUpdate(true);
				roomEntity.setNext(next);

			}
			else {
				roomEntity.setNext(null);
				roomEntity.setNeedUpdate(true);
			}
		}
	}

}
