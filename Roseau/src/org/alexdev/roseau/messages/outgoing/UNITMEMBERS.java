package org.alexdev.roseau.messages.outgoing;

import java.util.List;

import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class UNITMEMBERS implements OutgoingMessageComposer {

	private List<Entity> entities;

	public UNITMEMBERS(List<Entity> entities) {
		this.entities = entities;
	}

	@Override
	public void write(Response response) {
		response.init("UNITMEMBERS");
		
		for (Entity entity : this.entities) {
			response.appendNewArgument(entity.getDetails().getUsername());
		}
	}

}
