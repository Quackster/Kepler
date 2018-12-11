package org.alexdev.roseau.game.item.interactors.pool;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.Interaction;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.outgoing.JUMPINGPLACE_OK;

public class PoolLiftInteractor extends Interaction {

	public PoolLiftInteractor(Item item) {
		super(item);
	}

	@Override
	public void onTrigger(Player player) {	}

	@Override
	public void onStoppedWalking(Player player) {
		
		this.close();

		player.send(new JUMPINGPLACE_OK());
		player.getRoomUser().setCanWalk(false);

		player.getDetails().setTickets(player.getDetails().getTickets() - 1);
		player.getDetails().sendTickets();
		player.getDetails().save();
	}
	
	public void open() {
		this.item.showProgram("open");
		this.item.unlockTiles();
	}
	
	public void close() {
		this.item.showProgram("close");
		this.item.lockTiles();	
	}

}
