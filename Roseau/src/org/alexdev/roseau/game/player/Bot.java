package org.alexdev.roseau.game.player;

import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.model.Position;

public class Bot implements Entity {
	
	private PlayerDetails details;
	private RoomUser roomEntity;
	
	private Position startPosition;
	private int startRotation;

	public Bot(Position startPosition, int startRotation) {
		this.details = new PlayerDetails(this);
		this.roomEntity = new RoomUser(this);
		
		this.startPosition = startPosition;
		this.startRotation = startRotation;
	}
	
	public PlayerDetails getDetails() {
		return details;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.BOT;
	}
	
	@Override
	public RoomUser getRoomUser() {
		return this.roomEntity;
	}

	public Position getStartPosition() {
		return startPosition;
	}
	
	public int getStartRotation() {
		return startRotation;
	}
}
