package org.alexdev.roseau.game.room.settings;

public enum RoomState {
	
	OPEN(0, "open"),
	DOORBELL(1, "closed"),
	PASSWORD(2, "password");
	
	private int stateCode;
	private String toString;
	
	RoomState(int stateCode, String toString) {
		this.stateCode = stateCode;
		this.toString = toString;
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

	@Override
	public String toString() {
		return toString;
	}
}
