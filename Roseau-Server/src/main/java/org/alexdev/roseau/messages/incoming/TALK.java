package org.alexdev.roseau.messages.incoming;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.GameVariables;
import org.alexdev.roseau.game.player.Bot;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.ChatUtility;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.CHAT;
import org.alexdev.roseau.server.messages.ClientMessage;
import org.alexdev.roseau.util.Util;

public class TALK implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		player.getRoomUser().resetAfkTimer();
		
		if (player.getRoomUser().getRoom() == null) {
			return;
		}

		if (reader.getArgumentAmount() < 1) {
			return;
		}

		String talkMessage = Util.filterInput(reader.getMessageBody());

		if (!reader.getHeader().equals("CHAT") && !reader.getHeader().equals("SHOUT")  && !reader.getHeader().equals("WHISPER")) {
			return;
		}

		if (reader.getHeader().equals("WHISPER")) {

			// Handle whispers
			String[] args = talkMessage.split(" ");

			if (args.length > 1) {

				String username = args[0];
				String message = talkMessage.substring(username.length() + 1);

				Player whispered = Roseau.getGame().getPlayerManager().getByName(username);

				talkMessage = message;

				CHAT response = new CHAT("WHISPER", player.getDetails().getName(), message);
				player.send(response);

				if (whispered != null) {
					if (whispered.getDetails().getID() != player.getDetails().getID()) {
						whispered.send(response);
						
						Roseau.getDao().getRoom().saveChatlog(player, player.getRoomUser().getRoom().getData().getID(), reader.getHeader(), "(to: " + username + ") " + message);
					}
				}
			} else {
				CHAT response = new CHAT("WHISPER", player.getDetails().getName(), talkMessage);
				player.send(response);
			
			}
		} else {


			if (Roseau.getGame().getCommandManager().hasCommand(talkMessage)) {
				Roseau.getGame().getCommandManager().invokeCommand(player, talkMessage);
				return;
			}

			List<Player> players = null;

			if (reader.getHeader().equals("SHOUT")) {
				players = player.getRoomUser().getRoom().getPlayers();
			}

			if (reader.getHeader().equals("CHAT")) {
				players = player.getRoomUser().getRoom().getMapping().getNearbyPlayers(player, player.getRoomUser().getPosition(), GameVariables.TALK_DISTANCE);
			}

			CHAT chat = new CHAT(reader.getHeader(), player.getDetails().getName(), talkMessage);

			for (Player roomPlayer : players) {
				
				if (roomPlayer != player) {
					roomPlayer.getRoomUser().lookTowards(player.getRoomUser().getPosition());
					roomPlayer.getRoomUser().setLookResetTime(GameVariables.TALK_LOOKAT_RESET);
				}
				
				roomPlayer.send(chat);
			}

			if (reader.getHeader().equals("CHAT")) {
				player.send(chat);
			}
		}

		if (reader.getHeader().equals("CHAT") || reader.getHeader().equals("SHOUT")) {

			int talkDuration = 1;

			if (talkMessage.length() > 1) {
				if (talkMessage.length() >= 10) {
					talkDuration = 5;
				} else {
					talkDuration = talkMessage.length() / 2;
				}
			}
			
			String emote = ChatUtility.detectEmote(talkMessage.split(" "));
			
			if (emote != null) {
				player.getRoomUser().setStatus("gest", " " + emote, false, 5);
			}
			
			player.getRoomUser().setStatus("talk", "", false, talkDuration, true);

			for (Bot bot : player.getRoomUser().getRoom().getMapping().getNearbyBots(player, player.getRoomUser().getPosition(), 5)) {
				String trigger = bot.containsTrigger(talkMessage);

				if (trigger != null) {
					bot.getRoomUser().chat(bot.getResponse(player.getDetails().getName(), trigger), 2);
					player.getRoomUser().setStatus("carryd", " " + trigger, false, GameVariables.CARRY_DRINK_TIME, true);
				} else {

					if ((talkMessage.toLowerCase().contains("hello") || talkMessage.toLowerCase().contains("hi")) && talkMessage.toLowerCase().contains(bot.getDetails().getName().toLowerCase())) {
						bot.getRoomUser().chat("Hello " + player.getDetails().getName() + "!", 2);
					}

					if ((talkMessage.toLowerCase().contains("hello") || talkMessage.toLowerCase().contains("hi")) && !talkMessage.toLowerCase().contains(bot.getDetails().getName().toLowerCase())) {
						bot.getRoomUser().chat("Hello there!", 2);
					} 
				}
			}
			
			Roseau.getDao().getRoom().saveChatlog(player, player.getRoomUser().getRoom().getData().getID(), reader.getHeader(), talkMessage);
		}
	}

}
