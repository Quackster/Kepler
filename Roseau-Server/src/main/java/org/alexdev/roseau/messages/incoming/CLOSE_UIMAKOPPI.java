package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.pool.PoolChangeBoothInteractor;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class CLOSE_UIMAKOPPI implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		if (player.getRoomUser().getRoom() == null) {
			return;
		}
		
		Room room = player.getRoomUser().getRoom();
		Position position = player.getRoomUser().getPosition();
		
		Item item = room.getMapping().getHighestItem(position.getX(), position.getY());

		if (item != null) {
			if (item.getDefinition().getSprite().equals("poolBooth")) {
				
				PoolChangeBoothInteractor interactor = (PoolChangeBoothInteractor)item.getInteraction();
				interactor.open();
				
				Position walk = new Position(item.getCustomData());
				
				player.getRoomUser().setCanWalk(true);
				player.getRoomUser().walkTo(walk.getX(), walk.getY());
				
			}
		}
	}
}
