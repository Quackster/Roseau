package org.alexdev.roseau.messages.outgoing.room;

import java.util.Arrays;
import java.util.List;

import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class USERS implements OutgoingMessageComposer {

	private List<IEntity> entities;
	
	public USERS(IEntity entity) {
		this.entities = Arrays.asList(new IEntity[] { entity });
	}
	
	public USERS(List<IEntity> entities) {
		this.entities = entities;
	}

	@Override
	public void write(Response response) {
		response.init("USERS");
		for (IEntity entity : this.entities) {
			response.append(Character.toString((char)13));
			response.appendArgument("");
			response.appendArgument(entity.getDetails().getUsername());
			response.appendArgument(entity.getDetails().getFigure());
			response.appendArgument(String.valueOf(entity.getRoomEntity().getPosition().getX()));
			response.appendArgument(String.valueOf(entity.getRoomEntity().getPosition().getY()));
			response.appendArgument(String.valueOf(entity.getRoomEntity().getPosition().getZ()));
			response.appendArgument(entity.getDetails().getMission());
		}
	}

}
