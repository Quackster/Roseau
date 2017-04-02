package org.alexdev.roseau.messages.outgoing.room;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class STATUS implements OutgoingMessageComposer {

	private List<IEntity> entities;

	public STATUS(IEntity entity) {
		this.entities = Arrays.asList(new IEntity[] { entity });
	}

	public STATUS(List<IEntity> entities) {
		this.entities = entities;
	}

	@Override
	public void write(Response response) {
		response.init("STATUS ");
		
		for (IEntity entity : this.entities) {
			response.appendNewArgument(entity.getDetails().getUsername());
			response.appendArgument(String.valueOf(entity.getRoomUser().getPosition().getX()));
			response.appendArgument(String.valueOf(entity.getRoomUser().getPosition().getY()), ',');
			
			if (entity.getRoomUser().isWalking()) {
				if (entity.getRoomUser().getNext() == null) {
					entity.getRoomUser().stopWalking();
				}
			}
			
			response.appendArgument(String.valueOf((int)entity.getRoomUser().getPosition().getZ()), ',');			
			response.appendArgument(String.valueOf(entity.getRoomUser().getHeadRotation()), ',');
			response.appendArgument(String.valueOf(entity.getRoomUser().getRotation()), ',');
			
			String status = "/";

			for (Entry<String, String> set : entity.getRoomUser().getStatuses().entrySet()) {
				status += set.getKey() + set.getValue() + "/";
			}
			
			response.append(status);
			
			/*if (entity.getRoomUser().needsUpdate()) {
				entity.getRoomUser().setNeedUpdate(false);
			}*/
		}
	}

}
