package org.alexdev.roseau.game.player;

import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.IPlayerNetwork;

public class Player implements IEntity {

	private String machineId;
	private PlayerDetails details;
	private IPlayerNetwork network;

	public Player(IPlayerNetwork network) {
		this.network = network;
		this.details = new PlayerDetails(this);
	}
	
	public void dispose() {

	}
		
	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}
		
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	public String getMachineId() {
		return machineId;
	}

	public PlayerDetails getDetails() {
		return details;
	}

	public IPlayerNetwork getNetwork() {
		return network;
	}

	public void send(OutgoingMessageComposer response) {
		this.network.send(response);
		
	}

}
