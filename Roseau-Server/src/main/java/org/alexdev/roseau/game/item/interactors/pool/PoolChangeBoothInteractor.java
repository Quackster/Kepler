package org.alexdev.roseau.game.item.interactors.pool;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.Interaction;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.outgoing.OPEN_UIMAKOPPI;

public class PoolChangeBoothInteractor extends Interaction {

	public PoolChangeBoothInteractor(Item item) {
		super(item);
	}

	@Override
	public void onTrigger(Player player) {	}

	@Override
	public void onStoppedWalking(Player player) {
		
		this.close();
		
		player.send(new OPEN_UIMAKOPPI());
		player.getRoomUser().setCanWalk(false);
		
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
