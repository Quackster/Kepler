package org.alexdev.roseau.game.catalogue;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.item.ItemDefinition;

public class CatalogueItem {

	private int credits;
	private int definitionID;
	private String callID;
	private String extraData;
	
	public CatalogueItem(String callID, int definitionID, int credits) {
		this.callID = callID;
		this.credits = credits;
		this.definitionID = definitionID;
		this.extraData = null;
	}

	public String getCallID() {
		return callID;
	}

	public int getCredits() {
		return credits;
	}

	public ItemDefinition getDefinition() {
		return Roseau.getGame().getItemManager().getDefinition(this.definitionID);
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}
}
