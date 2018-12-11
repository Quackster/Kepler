package org.alexdev.roseau.game.item;

import java.util.HashMap;
import java.util.Map;

import org.alexdev.roseau.Roseau;

public class ItemManager {
	private Map<Integer, ItemDefinition> definitions;

	public ItemManager() {
		this.definitions = new HashMap<Integer, ItemDefinition>();
	}

	public void load() {
		this.loadDefinitions();
	}
	
	private void loadDefinitions() {
		if (this.definitions != null) {
			this.definitions.clear();
			this.definitions = null;
		}
		
		this.definitions = Roseau.getDao().getItem().getDefinitions();
	}

	public ItemDefinition getDefinition(int definitionID) {
		return definitions.get(definitionID);
	}
	
}
