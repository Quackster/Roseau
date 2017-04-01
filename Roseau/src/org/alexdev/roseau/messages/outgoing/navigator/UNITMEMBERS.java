package org.alexdev.roseau.messages.outgoing.navigator;

import java.util.List;

import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class UNITMEMBERS implements OutgoingMessageComposer {

	private List<IEntity> entities;

	public UNITMEMBERS(List<IEntity> entities) {
		this.entities = entities;
	}

	@Override
	public void write(Response response) {
		response.init("UNITMEMBERS");
		
		for (IEntity entity : this.entities) {
			response.appendNewArgument(entity.getDetails().getUsername());
		}
	}

}
