package org.alexdev.roseau.game.item.interactors;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;

public class BlankInteractor extends Interaction {

	public BlankInteractor(Item item) {
		super(item);
	}

	@Override
	public void onTrigger(Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStoppedWalking(Player player) {
		player.getRoomUser().removeStatus("sit");
		player.getRoomUser().removeStatus("lay");
	}


}
