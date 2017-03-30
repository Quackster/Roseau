package org.alexdev.roseau.game.entity;

import org.alexdev.roseau.game.player.Player;

public enum EntityType {

	PLAYER(Player.class),
	PET(IEntity.class),
	BOT(IEntity.class);
	
	Class<? extends IEntity> clazz;
	
	EntityType(Class<? extends IEntity> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends IEntity> getClazz() {
		return clazz;
	}
	
}
