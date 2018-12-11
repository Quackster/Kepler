package org.alexdev.roseau.game.room.schedulers;

import org.alexdev.roseau.game.room.Room;

public abstract class RoomEvent {

	protected Room room;
	protected long ticked;

	public RoomEvent(Room room) {
		this.room = room;
		this.ticked = 0;
	}
	
	public void increaseTicked() {
		this.ticked++;
	}
	
	public boolean canTick(int secondInterval) {
		return this.ticked % secondInterval == 0;
	}
	
	public abstract void tick();
}
