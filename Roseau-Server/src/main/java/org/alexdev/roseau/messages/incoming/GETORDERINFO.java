package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.catalogue.CatalogueDeal;
import org.alexdev.roseau.game.catalogue.CatalogueItem;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.ORDERINFO;
import org.alexdev.roseau.server.messages.ClientMessage;

public class GETORDERINFO implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		String callID = reader.getMessageBody().substring(4).replace(" " + player.getDetails().getName(), "");
		String catalogueID = callID;
		
		if (callID.contains("L ") || callID.contains("T ") || callID.contains("juliste ")) {
			catalogueID = callID.split(" ")[0];
		}
		
		
		CatalogueItem item = Roseau.getGame().getCatalogueManager().getItemByCall(catalogueID);
		CatalogueDeal deal = Roseau.getGame().getCatalogueManager().getDealByCall(catalogueID);
		
		boolean validOrderInfo = false;

		if (deal != null) {
			
			player.send(new ORDERINFO(deal.getCallID(), deal.getCost()));
			validOrderInfo = true;

		} else if (item != null) {
			
			ItemDefinition definition = item.getDefinition();

			if (definition == null) {
				return;
			}

			String extraData = "";

			if (callID.contains("L ") || callID.contains("T ") || callID.contains("juliste ")) {
				extraData += " " + callID.split(" ")[1];
			}

			player.send(new ORDERINFO(item.getCallID() + extraData, item.getCredits()));
			validOrderInfo = true;
		}
		
		if (validOrderInfo) {
			player.setOrderInfoProtection(DateTime.getTime());
		}
	}

}
