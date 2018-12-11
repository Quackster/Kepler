package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class SETSTRIPITEMDATA implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		int itemID = Integer.valueOf(reader.getArgument(1, Character.toString((char)13)));
		//int amountLeft = Integer.valueOf(reader.getArgument(1, Character.toString((char)13)));
		
		Item item = player.getInventory().getItem(itemID);
		
		if (!item.getDefinition().getBehaviour().isPostIt()) {
			return;
		}
		
		int currentAmount = Integer.valueOf(item.getCustomData());
		int newAmount = currentAmount - 1;
		
		if (newAmount > 0) {
			item.setCustomData(Integer.toString(newAmount));
			item.save();
		} else {
			player.getInventory().removeItem(item);
			player.getInventory().refresh("last");
			item.delete();
		}
	}

}
