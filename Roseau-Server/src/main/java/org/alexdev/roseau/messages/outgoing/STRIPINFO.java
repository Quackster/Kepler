package org.alexdev.roseau.messages.outgoing;

import java.util.List;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class STRIPINFO extends OutgoingMessageComposer {

	private List<Item> items;

	public STRIPINFO(List<Item> items) {
		this.items = items;
	}

	public STRIPINFO() {
		this.items = null;
	}

	@Override
	public void write() {
		response.init("STRIPINFO");


		int slotID = 0;
		if (items != null) {
			for (Item item : items) {

				response.appendNewArgument("roseau");
				response.appendArgument(String.valueOf(item.getID()), ';');
				response.appendArgument("0", ';');

				if (item.getDefinition().getBehaviour().isSTUFF()) {
					response.appendArgument("S", ';');
				} else if (item.getDefinition().getBehaviour().isITEM()) {
					response.appendArgument("I", ';');
				}

				response.appendArgument(String.valueOf(slotID), ';');
				response.appendArgument(item.getDefinition().getSprite(), ';');
				response.appendArgument(item.getDefinition().getName(), ';');

				if (item.getDefinition().getBehaviour().isSTUFF()) {

					response.appendArgument(item.getCustomData(), ';');
					response.appendArgument(String.valueOf(item.getDefinition().getLength()), ';');
					response.appendArgument(String.valueOf(item.getDefinition().getWidth()), ';');
					response.appendArgument(item.getDefinition().getColor(), ';');

				} else if (item.getDefinition().getBehaviour().isITEM()) {

					if (item.getDefinition().getBehaviour().isPostIt()) {
						if (item.getCustomData().equals("1")) {
							response.appendArgument("2", ';');
							response.appendArgument("2", ';');
						} else {
							response.appendArgument(item.getCustomData(), ';');
							response.appendArgument(item.getCustomData(), ';');
						}
					}  else {
						response.appendArgument(item.getCustomData(), ';');
						response.appendArgument(item.getCustomData(), ';');
					}
				} else {
					response.appendArgument(item.getCustomData(), ';');
					response.appendArgument(item.getCustomData(), ';');
				}

				response.appendArgument("", '/');
			}

			slotID++;
		}
	}
}
