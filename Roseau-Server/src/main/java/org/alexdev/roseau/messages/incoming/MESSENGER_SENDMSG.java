package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.messenger.MessengerUser;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.MESSENGER_MSG;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MESSENGER_SENDMSG implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		String[] data = reader.getMessageBody().split("\r", 2);
		String[] szReceiverIDs = data[0].split(" ");
		
		int[] receiverIDs = new int[szReceiverIDs.length];
		
		for(int i = 0; i < receiverIDs.length; i++)	{
			
			try	{
				int receiverID = Integer.parseInt(szReceiverIDs[i]);
				receiverIDs[i] = receiverID;
			} catch(NumberFormatException ex) {
				Log.println("scripting at MESSENGER_SENDMSG");
				return;
			}
		}
		
		String message = data[1].replace((char)13, (char)10);
		
		for (int friendID : receiverIDs) {
			
			MessengerUser user = player.getMessenger().getFriend(friendID);
			
			if (user == null) {
				continue;
			}
			
			int messageID = Roseau.getDao().getMessenger().newMessage(player.getDetails().getID(), friendID, message);
			
			if (user.isOnline()) {
				user.getPlayer().send(new MESSENGER_MSG(messageID, player.getDetails().getID(), DateTime.getTime(), message, player.getDetails().getFigure()));
			}
		}
	}

}
