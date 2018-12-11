package org.alexdev.roseau.game.item.interactors.pool;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.Interaction;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.model.Position;

public class PoolQueueInteractor extends Interaction {

	public PoolQueueInteractor(Item item) {
		super(item);
	}

	@Override
	public void onTrigger(Player player) {	}

	@Override
	public void onStoppedWalking(Player player) {
		
		Position next = new Position(this.item.getCustomData());
		player.getRoomUser().walkTo(next.getX(), next.getY());
	}
}
