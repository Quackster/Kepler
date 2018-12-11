package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.catalogue.CatalogueDeal;
import org.alexdev.roseau.game.catalogue.CatalogueItem;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.PURCHASE_ADDSTRIPITEM;
import org.alexdev.roseau.messages.outgoing.SYSTEMBROADCAST;
import org.alexdev.roseau.server.messages.ClientMessage;
import org.alexdev.roseau.util.Util;

public class PURCHASE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (!(DateTime.getTime() - player.getOrderInfoProtection() > 500)) {
			return;
		}

		String callID = reader.getMessageBody();

		if (!callID.contains("hyppy")) {
			callID = callID.replace(" " + player.getDetails().getName(), "");
		}

		callID = callID.replace("/", "");

		Player p = player.getPrivateRoomPlayer();
		CatalogueItem product = null;

		if (callID.contains("hyppy")) {

			int oldCredits = player.getDetails().getCredits();

			if (oldCredits < 10) {
				player.sendAlert("Sorry, but you do not have enough Credits to purchase this.");
				return;
			}

			String target = Util.filterInput(callID.split(" ")[2]);

			PlayerDetails details = Roseau.getDao().getPlayer().getDetails(target);

			if (details == null) {
				player.sendAlert("The player '" + target + "' cannot be found.");
				return;
			} else {

				Player targetPlayer = Roseau.getGame().getPlayerManager().getByIDMainServer(details.getID());

				if (targetPlayer == null) {
					details.setTickets(details.getTickets() + 10);
					details.save();
				} else {
					
					int newTickets = targetPlayer.getDetails().getTickets() + 10;
					
					targetPlayer.getDetails().setTickets(newTickets);
					targetPlayer.getDetails().sendTickets();
					targetPlayer.getDetails().save();

					if (targetPlayer.getDetails().getID() != player.getDetails().getID()) {
						player.sendAlert("You have bought 10 game tickets for " + targetPlayer.getDetails().getName());
						targetPlayer.sendAlert(player.getDetails().getName() + " has bought 10 game tickets for you!");
					} else {
						player.sendAlert("You have bought 10 game tickets!");
					}
					
					if (targetPlayer.getPublicRoomPlayer() != null) {
						targetPlayer.getPublicRoomPlayer().getDetails().setTickets(newTickets);
					}
					
					//Roseau.getGame().getPlayerManager().syncPlayerTickets(targetPlayer.getDetails().getId(), newTickets);
				}

				player.getDetails().setCredits(player.getDetails().getCredits() - 5);
				player.getDetails().sendCredits();
				player.getDetails().save();
			}

			return;
		}

		if (callID.contains("L ") || callID.contains("T ") || callID.contains("juliste ")) {
			product = Roseau.getGame().getCatalogueManager().getItemByCall(callID.split(" ")[0]);
		} else {
			product = Roseau.getGame().getCatalogueManager().getItemByCall(callID);
		}

		if (product != null) {

			int oldCredits = player.getDetails().getCredits();

			if (oldCredits >= product.getCredits()) {

				Item item = Roseau.getDao().getInventory().newItem(product.getDefinition().getID(), player.getDetails().getID(), "");

				if (item.getDefinition().getBehaviour().isDecoration() || callID.contains("juliste ")) {
					item.setCustomData(callID.split(" ")[1]);
					item.save();
				}


				if (item.getDefinition().getBehaviour().isPostIt()) {
					item.setCustomData("20");
					item.save();
				}


				if (item.getDefinition().getBehaviour().isTeleporter()) {

					Item firstTeleporter = item;
					Item secondTeleporter = Roseau.getDao().getInventory().newItem(product.getDefinition().getID(), player.getDetails().getID(), "");

					firstTeleporter.setCustomData(String.valueOf(secondTeleporter.getID()));
					secondTeleporter.setCustomData(String.valueOf(firstTeleporter.getID()));
					
					firstTeleporter.save();
					secondTeleporter.save();

					p.getInventory().addItem(secondTeleporter);
				}

				p.getInventory().addItem(item);

				player.send(new PURCHASE_ADDSTRIPITEM());
				//player.send(new PURCHASEOK());

				player.getDetails().setCredits(player.getDetails().getCredits() - product.getCredits());
				player.getDetails().sendCredits();
				player.getDetails().save();

			} else {
				player.send(new SYSTEMBROADCAST("You don't have enough credits to purchase this item!"));
			}
		}

		CatalogueDeal deal = Roseau.getGame().getCatalogueManager().getDealByCall(callID);

		if (deal != null) {

			int oldCredits = player.getDetails().getCredits();

			if (oldCredits >= deal.getCost()) {

				for (CatalogueItem item : deal.getItems()) {
					Item newItem = Roseau.getDao().getInventory().newItem(item.getDefinition().getID(), player.getDetails().getID(), "");

					if (newItem == null) {
						continue;
					}

					if (item.getExtraData() != null) {
						newItem.setCustomData(item.getExtraData());
						newItem.save();
					}

					p.getInventory().addItem(newItem);
				}

				p.getInventory().load();

				player.send(new PURCHASE_ADDSTRIPITEM());

				player.getDetails().setCredits(player.getDetails().getCredits() - deal.getCost());
				player.getDetails().sendCredits();
				player.getDetails().save();

			} else {
				player.send(new SYSTEMBROADCAST("You don't have enough credits to purchase this item!"));
			}

		}

	}

}
