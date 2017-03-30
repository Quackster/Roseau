package org.alexdev.roseau.messages.outgoing.room;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class HEIGHTMAP implements OutgoingMessageComposer {

	private String heightMap;

	public HEIGHTMAP(String heightMap) {
		this.heightMap = heightMap;
	}

	@Override
	public void write(Response response) {
		response.init("HEIGHTMAP");
		response.appendNewArgument(this.heightMap);
	}

}
