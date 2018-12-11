package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class STUFFDATAUPDATE extends OutgoingMessageComposer {

	private Item item;
	private String customData;
	
	public STUFFDATAUPDATE(Item item, String customData) {
		this.item = item;
		this.customData = customData;
	}

	@Override
	public void write() {
		response.init("STUFFDATAUPDATE");
		response.appendNewArgument(this.item.getPadding());
		response.append(Integer.toString(this.item.getID()));
		response.appendPartArgument("");
		response.appendPartArgument(this.item.getDefinition().getDataClass());
		response.appendPartArgument(this.customData);
	}

}
