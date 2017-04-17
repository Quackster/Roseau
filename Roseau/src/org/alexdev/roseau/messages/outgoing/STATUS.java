package org.alexdev.roseau.messages.outgoing;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.room.entity.RoomUserStatus;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class STATUS implements OutgoingMessageComposer {

	private List<Entity> entities;

	public STATUS(Entity entity) {
		this.entities = Arrays.asList(new Entity[] { entity });
	}

	public STATUS(List<Entity> entities) {
		this.entities = entities;
	}

	@Override
	public void write(Response response) {
		response.init("STATUS ");
		
		for (Entity entity : this.entities) {
			response.appendNewArgument(entity.getDetails().getUsername());

			if (entity.getRoomUser().isWalking()) {
				if (entity.getRoomUser().getNext() == null) {
					entity.getRoomUser().stopWalking();
				}
			}
			response.appendArgument(String.valueOf(entity.getRoomUser().getPosition().getX()));
			response.appendArgument(String.valueOf(entity.getRoomUser().getPosition().getY()), ',');
			response.appendArgument(String.valueOf((int)entity.getRoomUser().getPosition().getZ()), ',');			
			response.appendArgument(String.valueOf(entity.getRoomUser().getPosition().getHeadRotation()), ',');
			response.appendArgument(String.valueOf(entity.getRoomUser().getPosition().getRotation()), ',');
			
			String status = "/";

			for (Entry<String, RoomUserStatus> set : entity.getRoomUser().getStatuses().entrySet()) {
				
				RoomUserStatus statusEntry = set.getValue();
				
				status += statusEntry.getStatus();
				status += statusEntry.getValue();
				status += "/";
			}
			
			response.append(status);
			
			/*if (entity.getRoomUser().needsUpdate()) {
				entity.getRoomUser().setNeedUpdate(false);
			}*/
		}
	}

}
