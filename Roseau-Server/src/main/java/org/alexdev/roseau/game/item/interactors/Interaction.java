package org.alexdev.roseau.game.item.interactors;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.game.player.Player;

public abstract class Interaction {

	protected Item item;
	protected ItemDefinition definition;

	public Interaction(Item item) {
		this.item = item;

		if (this.item != null) {
			this.definition = this.item.getDefinition();
		}
	}

	public abstract void onTrigger(Player player);
	public abstract void onStoppedWalking(Player player);
}
