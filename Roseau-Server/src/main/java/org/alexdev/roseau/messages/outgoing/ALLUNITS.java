package org.alexdev.roseau.messages.outgoing;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.OutgoingMessageComposer;

public class ALLUNITS extends OutgoingMessageComposer {

	private List<Room> publicRooms;

	public ALLUNITS(List<Room> publicRooms) {
		this.publicRooms = publicRooms;
		
		
	}

	@Override
	public void write() {
		response.init("ALLUNITS");
		
		/*
		SendData Index, "BUSY_FLAT_RESULTS 1" & Chr(13) & "1/Room Name/Room Owner/open/ekakerros/floor1/" & PrvServer & "/" & PrvServer & "/" & PrvPort & "/" & GetUserCount(1) & "/null/Room Description"
      	SendData Index, "ALLUNITS " & Chr(13) & "Habbo Lido,0,25," & PrvServer & "/" & PrvServer & ",22009,Habbo Lido" & vbTab & "lido,1,25,pool_a" & Chr(13)
      	*/
		
		for (Room room : publicRooms) {
			response.appendNewArgument(room.getData().getName());
			response.appendArgument(String.valueOf(room.getData().getUsersNow()), ',');
			response.appendArgument(String.valueOf(room.getData().getUsersMax()), ',');
			response.appendArgument(Roseau.getServerIP(), ',');
			response.appendArgument(Roseau.getServerIP(), '/');
			response.appendArgument(String.valueOf(room.getData().getServerPort()), ',');
			response.appendArgument(room.getData().getName(), ',');
			response.appendTabArgument(room.getData().getCCT());
			response.appendArgument(String.valueOf(room.getData().getUsersNow()), ',');
			response.appendArgument(String.valueOf(room.getData().getUsersMax()), ',');
			response.appendArgument(room.getData().getModelName(), ',');
		}
	}

}
