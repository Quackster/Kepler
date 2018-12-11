package org.alexdev.roseau.dao;

import java.util.Map;

import org.alexdev.roseau.game.catalogue.CatalogueDeal;
import org.alexdev.roseau.game.catalogue.CatalogueItem;

public interface CatalogueDao {

	public Map<String, CatalogueItem> getBuyableItems();
	public Map<String, CatalogueDeal> getItemDeals();

}
