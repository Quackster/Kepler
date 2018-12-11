package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.ALLUNITS;
import org.alexdev.roseau.server.messages.ClientMessage;

public class INITUNITLISTENER implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		/*
		SendData Index, "BUSY_FLAT_RESULTS 1" & Chr(13) & "1/Room Name/Room Owner/open/ekakerros/floor1/" & PrvServer & "/" & PrvServer & "/" & PrvPort & "/" & GetUserCount(1) & "/null/Room Description"
      	SendData Index, "ALLUNITS " & Chr(13) & "Habbo Lido,0,25," & PrvServer & "/" & PrvServer & ",22009,Habbo Lido" & vbTab & "lido,1,25,pool_a" & Chr(13)
      	*/
		
		//player.send(new CRYFORHELP());
		player.send(new ALLUNITS(Roseau.getGame().getRoomManager().getPublicRooms()));
	
	}
}
