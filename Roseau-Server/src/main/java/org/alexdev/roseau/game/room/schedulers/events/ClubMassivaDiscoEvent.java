package org.alexdev.roseau.game.room.schedulers.events;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.schedulers.RoomEvent;
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;
import org.alexdev.roseau.util.Util;

public class ClubMassivaDiscoEvent extends RoomEvent {

	private int currentLampID;

	public ClubMassivaDiscoEvent(Room room) {
		super(room);
		this.currentLampID = -1;
	}

	@Override
	public void tick() {

		if (this.canTick(10)) { // 30 seconds 
			
			this.currentLampID = this.getNewLampID();
			room.send(new SHOWPROGRAM(new String[] {"lamp", "setlamp", String.valueOf(this.currentLampID)}));
			
			String discoID = String.valueOf(Util.getRandom().nextInt(14) + 1);
			room.send(new SHOWPROGRAM(new String[] {"df1", "setfloora", discoID }));
			room.send(new SHOWPROGRAM(new String[] {"df2", "setfloora", discoID }));
			room.send(new SHOWPROGRAM(new String[] {"df3", "setfloora", discoID }));
			
			boolean otherPaletes = Util.getRandom().nextBoolean();

			if (otherPaletes) {
				room.send(new SHOWPROGRAM(new String[] {"df1", "setfloorb", discoID }));
				room.send(new SHOWPROGRAM(new String[] {"df2", "setfloorb", discoID }));
				room.send(new SHOWPROGRAM(new String[] {"df3", "setfloorb", discoID }));
			}
		}
		
		this.increaseTicked();
	}

	private int getNewLampID() {
		
		int lampID = Util.getRandom().nextInt(5) + 1;
		
		if (lampID == this.currentLampID) {
			return this.getNewLampID();
		}
		
		return lampID;
	}

}
