package org.alexdev.roseau.game.player;

import org.alexdev.roseau.game.entity.EntityType;

import java.util.List;

import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.model.Position;

import com.google.common.collect.Lists;

public class Bot implements Entity {
	
	private PlayerDetails details;
	private RoomUser roomEntity;
	
	private Position startPosition;
	private List<int[]> positions;
	
	public Bot(Position startPosition, List<int[]> positions) {
		this.details = new PlayerDetails(this);
		this.roomEntity = new RoomUser(this);
		
		this.positions = positions;
		this.startPosition = startPosition;
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

	public List<int[]> getPositions() {
		return positions;
	}
}
