package org.alexdev.roseau.game.entity;

import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.entity.RoomEntity;

public interface IEntity {

	public PlayerDetails getDetails();
	public RoomEntity getRoomEntity();
	public EntityType getType();
}
