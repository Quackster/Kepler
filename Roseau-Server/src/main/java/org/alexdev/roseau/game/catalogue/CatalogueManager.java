package org.alexdev.roseau.game.catalogue;

import java.util.Map;

import org.alexdev.roseau.Roseau;

public class CatalogueManager {

	private Map<String, CatalogueItem> items;
	private Map<String, CatalogueDeal> deals;
	
	public void load() {
		this.items = Roseau.getDao().getCatalogue().getBuyableItems();
		this.deals = Roseau.getDao().getCatalogue().getItemDeals();
	}

	public CatalogueItem getItemByCall(String callID) {
		if (this.items.containsKey(callID)) {
			return this.items.get(callID);
		}
		
		return null;
	}
	
	public CatalogueDeal getDealByCall(String callID) {
		
		if (this.deals.containsKey(callID)) {
			return this.deals.get(callID);
		}
		
		return null;
	}
	
	public Map<String, CatalogueItem> getItems() {
		return items;
	}
}
