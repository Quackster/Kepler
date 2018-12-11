package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;
import org.alexdev.roseau.util.Util;

public class UPDATE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		if (reader.getMessageBody().contains("ph_figure")) {

			String poolFigure = reader.getMessageBody().substring(10);
			
			player.getDetails().setPoolFigure(poolFigure);
			player.getDetails().save();
			
			if (player.getRoomUser().getRoom() != null) {
				player.getRoomUser().getRoom().send(player.getRoomUser().getUsersComposer());
			}
		} else {
			
			String password = reader.getArgument(1, Character.toString((char)13)).split("=")[1];
			String email = reader.getArgument(2, Character.toString((char)13)).split("=")[1];
			String figure = reader.getArgument(3, Character.toString((char)13)).substring(7); // remove "figure="
			String mission = Util.filterInput(reader.getArgument(7, Character.toString((char)13)).substring(11));// remove "customData=" in case they put a = in their motto
			String sex = reader.getArgument(9, Character.toString((char)13)).split("=")[1];
			
			if (!sex.equals(player.getDetails().getSex())) {
				
				// Changed sex? Then we remove their opposite sex pool figure
				player.getDetails().setPoolFigure("");
			}
			
			if (email.length() > 256) {
				email = email.substring(0, 256);
			}
			
			if (mission.length() > 100) {
				mission = mission.substring(0, 100);
			}
			
			if (sex.length() < 4) {
				return;
			}
			
			if (sex.length() > 6) {
				return;
			}
			
			if (password.length() < 3) {
				return;
			}
			
			if (figure.length() < 3) {
				return;
			}
			
			player.getDetails().setPassword(password);
			player.getDetails().setEmail(email);
			player.getDetails().setFigure(figure);
			player.getDetails().setMission(mission);
			player.getDetails().setSex(sex);
			player.getDetails().save();
		}
	}

}
