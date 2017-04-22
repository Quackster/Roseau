package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.catalogue.CatalogueDeal;
import org.alexdev.roseau.game.catalogue.CatalogueItem;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.PURCHASE_ADDSTRIPITEM;
import org.alexdev.roseau.messages.outgoing.SYSTEMBROADCAST;
import org.alexdev.roseau.server.messages.ClientMessage;

public class PURCHASE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (!(Roseau.getUtilities().getUnixTimestamp() - player.getOrderInfoProtection() > 500)) {
			Log.println("difference: " + (Roseau.getUtilities().getUnixTimestamp() - player.getOrderInfoProtection()));
			return;
		}
		
		String callID = reader.getArgument(0).replace("/", "");

		Player p = player.getPrivateRoomPlayer();
		CatalogueItem product = Roseau.getGame().getCatalogueManager().getItemByCall(callID);

		if (product != null) {

			int oldCredits = player.getDetails().getCredits();

			if (oldCredits >= product.getCredits()) {

				Item item = Roseau.getDao().getInventory().newItem(product.getDefinition().getID(), player.getDetails().getID(), "");

				if (item.getDefinition().getBehaviour().isDecoration() || callID.equals("juliste")) {
					item.setCustomData(reader.getArgument(1));
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

		if (!reader.getMessageBody().contains("deal ")) {
			return;
		}
		
		Log.println("call ID: " + reader.getMessageBody().split(" ")[1]);
		
		CatalogueDeal deal = Roseau.getGame().getCatalogueManager().getDealByCall(reader.getMessageBody().split(" ")[1]);

		if (deal != null) {

			int oldCredits = player.getDetails().getCredits();

			if (oldCredits >= deal.getCost()) {

				for (CatalogueItem item : deal.getItems()) {
					Item newItem = Roseau.getDao().getInventory().newItem(item.getDefinition().getID(), player.getDetails().getID(), "");
					p.getInventory().addItem(newItem);
				}

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
