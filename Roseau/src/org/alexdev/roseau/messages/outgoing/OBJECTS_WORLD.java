package org.alexdev.roseau.messages.outgoing;

import java.util.concurrent.ConcurrentHashMap;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class OBJECTS_WORLD implements OutgoingMessageComposer {

	private String model;
	private ConcurrentHashMap<Integer, Item> passiveObjects;

	public OBJECTS_WORLD(String model, ConcurrentHashMap<Integer, Item> passiveObjects) {
		this.model = model;
		this.passiveObjects = passiveObjects;
	}

	@Override
	public void write(Response response) {
		response.init(" OBJECTS WORLD 0");

		response.appendArgument(model);
		
		for (Item item : this.passiveObjects.values()) {
			if (item.getDefinition().getBehaviour().isPassiveObject()) {
				response.appendObject(item);
			}
		}
	}

}
