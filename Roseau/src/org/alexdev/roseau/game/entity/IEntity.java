package org.alexdev.roseau.game.entity;

import org.alexdev.roseau.game.player.PlayerDetails;

public interface IEntity {

	public PlayerDetails getDetails();
	public EntityType getType();
}
