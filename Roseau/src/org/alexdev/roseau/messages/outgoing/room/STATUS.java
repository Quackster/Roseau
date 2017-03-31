package org.alexdev.roseau.messages.outgoing.room;

import java.util.Arrays;
import java.util.List;

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
			response.appendArgument(String.valueOf(entity.getRoomUser().getPosition().getZ()), ',');
			response.appendArgument(String.valueOf(entity.getRoomUser().getHeadRotation()), ',');
			response.appendArgument(String.valueOf(entity.getRoomUser().getRotation()), ',');
			response.append("//");
		}
	}

}
