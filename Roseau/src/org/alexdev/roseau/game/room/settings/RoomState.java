package org.alexdev.roseau.game.room.settings;

public enum RoomState {
	
	OPEN(0),
	DOORBELL(1),
	PASSWORD(2);
	
	private int stateCode;

	RoomState(int stateCode) {
		this.stateCode = stateCode;
	}
	
	public int getStateCode() {
		return stateCode;	}
	
	public static RoomState getState(int stateCode) {
		
		for (RoomState state : values()) {
			if (state.getStateCode() == stateCode) {
				return state;
			}
		}
		
		return RoomState.OPEN;
	}
}
