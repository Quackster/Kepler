package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class OPEN_GAMEBOARD extends OutgoingMessageComposer {

	private String game;
	private String gameID;
	private Item item;

	public OPEN_GAMEBOARD(String game, String gameID, Item item) {
		this.game = game;
		this.gameID = gameID;
		this.item = item;
	}

	@Override
	public void write() {
		response.init("OPEN_GAMEBOARD");
		response.appendNewArgument(this.gameID);
		response.appendArgument(this.game, ';');
		response.appendArgument(" ", ';');
		response.appendTabArgument(String.valueOf(item.getID()));
		response.appendArgument(String.valueOf(item.getDefinition().getSprite()));
		response.appendArgument(String.valueOf(item.getPosition().getX()));
		response.appendArgument(String.valueOf(item.getPosition().getY()));
	}

}
