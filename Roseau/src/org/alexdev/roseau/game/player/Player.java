package org.alexdev.roseau.game.player;

import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.IPlayerNetwork;
import org.alexdev.roseau.server.messages.ClientMessage;

public class Player implements IEntity {

	private String machineId;
	private PlayerDetails details;
	private IPlayerNetwork network;

	public Player(IPlayerNetwork network) {

		this.network = network;
		this.details = new PlayerDetails(this);
	}
	
	public void invoke(short header, ClientMessage message) {
		//Icarus.getServer().getMessageHandler().getMessages().get(header).handle(this, message);
	}
	
	public void dispose() {
		
		this.details.dispose();
		this.details = null;

		this.machineId = null;
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

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	public IPlayerNetwork getNetwork() {
		return network;
	}

	public void send(OutgoingMessageComposer response) {
		this.network.send(response);
		
	}

}
