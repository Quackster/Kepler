package org.alexdev.roseau.game.commands.types;

import org.alexdev.roseau.game.commands.Command;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomUser;

public class SitCommand implements Command {

	@Override
	public void handleCommand(Player player, String message) {
		RoomUser roomUser = player.getRoomUser();
		
		if (roomUser.getRoom() != null) {
			if (roomUser.isWalking()) {
				return;
			}
			
			if (roomUser.containsStatus("sit")) {
				return;
			}
			
			int rotation = roomUser.getPosition().getRotation();
			
			if (rotation != 0 && rotation != 2 && rotation != 4 && rotation != 6) {
				return;
			}
			
			roomUser.removeStatus("dance");
			
			double height = roomUser.getRoom().getMapping().getTile(
										roomUser.getPosition().getX(), 
										roomUser.getPosition().getY()).getHeight();
			
			roomUser.setStatus("sit", " " + String.valueOf((int)height), true, -1);
			roomUser.setNeedUpdate(true);
		}
		
	}

}
